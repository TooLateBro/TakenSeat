package com.taken_seat.performance_service.performance.application.service;

import static com.taken_seat.performance_service.common.config.RedisCacheConfig.*;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taken_seat.common_service.dto.request.BookingSeatClientRequestDto;
import com.taken_seat.common_service.dto.response.BookingSeatClientResponseDto;
import com.taken_seat.common_service.dto.response.PerformanceEndTimeDto;
import com.taken_seat.common_service.dto.response.PerformanceStartTimeDto;
import com.taken_seat.common_service.exception.customException.PerformanceException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import com.taken_seat.performance_service.common.context.CurrentUserContext;
import com.taken_seat.performance_service.performance.application.dto.mapper.PerformanceResponseMapper;
import com.taken_seat.performance_service.performance.domain.model.Performance;
import com.taken_seat.performance_service.performance.domain.model.PerformanceSchedule;
import com.taken_seat.performance_service.performance.domain.model.ScheduleSeat;
import com.taken_seat.performance_service.performance.domain.validator.PerformanceExistenceValidator;
import com.taken_seat.performance_service.performance.presentation.dto.response.ScheduleSeatResponseDto;
import com.taken_seat.performance_service.performance.presentation.dto.response.SeatLayoutResponseDto;
import com.taken_seat.performance_service.performancehall.domain.model.SeatStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PerformanceClientService {

	private final PerformanceExistenceValidator performanceExistenceValidator;
	private final CurrentUserContext userContext;
	@Qualifier("deduplicationStringRedisTemplate")
	private final StringRedisTemplate redisTemplate;
	private final Duration seatStatusTtl;

	@Transactional
	public BookingSeatClientResponseDto updateSeatStatus(BookingSeatClientRequestDto request) {

		if (isDuplicate(request, "lock")) {
			return new BookingSeatClientResponseDto(
				null, false, "이미 처리 중인 요청입니다. 잠시만 기다려 주세요."
			);
		}

		Performance performance = performanceExistenceValidator.validateByPerformanceId(request.performanceId());
		PerformanceSchedule schedule = performance.getScheduleById(request.performanceScheduleId());
		ScheduleSeat scheduleSeat = schedule.getScheduleSeatById(request.scheduleSeatId());

		if (scheduleSeat.getSeatStatus() == SeatStatus.SOLDOUT) {
			log.warn("[Performance] 좌석 선점 - 실패 - scheduleSeatId={}, scheduleId={}, 이유=이미 선점됨",
				request.scheduleSeatId(), request.performanceScheduleId());
			return new BookingSeatClientResponseDto(null, false, "이미 선점된 좌석입니다.");
		}

		scheduleSeat.updateStatus(SeatStatus.SOLDOUT);

		Integer price = performance.findPriceByScheduleAndSeatType(
			request.performanceScheduleId(),
			scheduleSeat.getSeatType()
		);

		log.info("[Performance] 좌석 선점 - 성공 - scheduleSeatId={}, scheduleId={}, seatType={}, price={}",
			request.scheduleSeatId(), request.performanceScheduleId(), scheduleSeat.getSeatType(), price);

		return new BookingSeatClientResponseDto(price, true, "좌석 선점에 성공했습니다.");
	}

	@Transactional
	public BookingSeatClientResponseDto cancelSeatStatus(BookingSeatClientRequestDto request) {

		if (isDuplicate(request, "cancel")) {
			return new BookingSeatClientResponseDto(
				null, false, "이미 처리 중인 요청입니다. 잠시만 기다려 주세요."
			);
		}

		Performance performance = performanceExistenceValidator.validateByPerformanceId(request.performanceId());
		PerformanceSchedule schedule = performance.getScheduleById(request.performanceScheduleId());
		ScheduleSeat scheduleSeat = schedule.getScheduleSeatById(request.scheduleSeatId());

		if (scheduleSeat.getSeatStatus() == SeatStatus.DISABLED) {
			log.warn("[Performance] 좌석 선점 취소 - 실패 - scheduleSeatId={}, 이유=변경 불가 상태(DISABLED)",
				request.scheduleSeatId());
			throw new PerformanceException(ResponseCode.SEAT_STATUS_CHANGE_NOT_ALLOWED);
		}

		scheduleSeat.updateStatus(SeatStatus.AVAILABLE);

		log.info("[Performance] 좌석 선점 취소 - 성공 - scheduleSeatId={}, scheduleId={}",
			request.scheduleSeatId(), request.performanceScheduleId());

		return new BookingSeatClientResponseDto(null, false, "좌석 선점이 취소되었습니다.");
	}

	@Transactional(readOnly = true)
	public SeatLayoutResponseDto getSeatLayout(UUID performanceScheduleId) {

		Performance performance = performanceExistenceValidator.validateByPerformanceScheduleId(performanceScheduleId);
		PerformanceSchedule schedule = performance.getScheduleById(performanceScheduleId);

		log.info("[Performance] 좌석 배치도 조회 - scheduleId={}, 좌석 수={}",
			performanceScheduleId, schedule.getScheduleSeats().size());

		List<ScheduleSeatResponseDto> seatLayout =
			PerformanceResponseMapper.toSeatLayout(schedule.getScheduleSeats());

		return new SeatLayoutResponseDto(seatLayout);
	}

	@Cacheable(
		cacheNames = SCHEDULE_END_TIME,
		key = "#performanceScheduleId",
		unless = "#result == null"
	)
	@Transactional
	public PerformanceEndTimeDto getPerformanceEndTime(UUID performanceId, UUID performanceScheduleId) {

		Performance performance = performanceExistenceValidator.validateByPerformanceId(performanceId);

		PerformanceSchedule schedule = performance.getScheduleById(performanceScheduleId);

		return new PerformanceEndTimeDto(schedule.getEndAt());
	}

	@Cacheable(
		cacheNames = SCHEDULE_START_TIME,
		key = "#performanceScheduleId",
		unless = "#result == null"
	)
	@Transactional
	public PerformanceStartTimeDto getPerformanceStartTime(UUID performanceId, UUID performanceScheduleId) {

		Performance performance = performanceExistenceValidator.validateByPerformanceId(performanceId);

		PerformanceSchedule schedule = performance.getScheduleById(performanceScheduleId);

		return new PerformanceStartTimeDto(schedule.getStartAt());
	}

	/**
	 * @return true 면 중복, false 면 처음 요청
	 */
	private boolean isDuplicate(BookingSeatClientRequestDto request, String action) {

		UUID userId = userContext.getUserId();

		String key = String.format(
			"dedup:perf:%s:sched:%s:seat:%s:user:%s:action:%s",
			request.performanceId(),
			request.performanceScheduleId(),
			request.scheduleSeatId(),
			userId,
			action
		);

		Boolean first = redisTemplate.opsForValue()
			.setIfAbsent(key, "1", seatStatusTtl);

		return !Boolean.TRUE.equals(first);
	}
}
