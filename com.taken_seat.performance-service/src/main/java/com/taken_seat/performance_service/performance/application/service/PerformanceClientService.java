package com.taken_seat.performance_service.performance.application.service;

import static com.taken_seat.performance_service.common.config.RedisCacheConfig.*;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taken_seat.common_service.aop.TrackLatency;
import com.taken_seat.common_service.dto.request.BookingSeatClientRequestDto;
import com.taken_seat.common_service.dto.response.BookingSeatClientResponseDto;
import com.taken_seat.common_service.dto.response.PerformanceEndTimeDto;
import com.taken_seat.common_service.dto.response.PerformanceStartTimeDto;
import com.taken_seat.common_service.exception.customException.PerformanceException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import com.taken_seat.performance_service.common.context.CurrentUserContext;
import com.taken_seat.performance_service.performance.application.dto.mapper.PerformanceResponseMapper;
import com.taken_seat.performance_service.performance.application.helper.SeatStatusKafkaHelper;
import com.taken_seat.performance_service.performance.application.helper.SeatStatusRedisHelper;
import com.taken_seat.performance_service.performance.domain.model.Performance;
import com.taken_seat.performance_service.performance.domain.model.PerformanceSchedule;
import com.taken_seat.performance_service.performance.domain.model.ScheduleSeat;
import com.taken_seat.performance_service.performance.domain.repository.PerformanceQueryRepository;
import com.taken_seat.performance_service.performance.domain.repository.redis.PerformanceRankingRedisRepository;
import com.taken_seat.performance_service.performance.domain.validator.PerformanceExistenceValidator;
import com.taken_seat.performance_service.performance.presentation.dto.response.ScheduleSeatResponseDto;
import com.taken_seat.performance_service.performance.presentation.dto.response.SeatLayoutResponseDto;
import com.taken_seat.performance_service.performancehall.domain.model.SeatStatus;

import io.micrometer.core.instrument.MeterRegistry;
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
	private final SeatStatusRedisHelper seatStatusRedisHelper;
	private final SeatStatusKafkaHelper seatStatusKafkaHelper;
	private final PerformanceQueryRepository performanceQueryRepository;
	private final MeterRegistry meterRegistry;
	private final PerformanceRankingRedisRepository performanceRankingRedisRepository;

	@TrackLatency(
		value = "performance_seat_lock_seconds",
		description = "좌석 선점 API 처리 시간(초)"
	)
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

		seatStatusRedisHelper.saveSeatStatus(
			request.performanceScheduleId(),
			request.scheduleSeatId(),
			SeatStatus.SOLDOUT);

		seatStatusKafkaHelper.sendSeatStatusChangedEvent(
			request.performanceScheduleId(),
			request.scheduleSeatId(),
			SeatStatus.SOLDOUT
		);

		Integer price = performance.findPriceByScheduleAndSeatType(
			request.performanceScheduleId(),
			scheduleSeat.getSeatType()
		);

		performanceRankingRedisRepository.incrementScore(request.performanceId(), 5.0);

		log.info("[Performance] 좌석 선점 - 성공 - scheduleSeatId={}, scheduleId={}, seatType={}, price={}",
			request.scheduleSeatId(), request.performanceScheduleId(), scheduleSeat.getSeatType(), price);

		meterRegistry.counter("performance_seat_lock_total", "result", "success").increment();
		meterRegistry.counter("performance_seat_lock_total", "result", "fail").increment();

		return new BookingSeatClientResponseDto(price, true, "좌석 선점에 성공했습니다.");
	}

	@TrackLatency(
		value = "performance_seat_cancel_seconds",
		description = "좌석 선점 취소 API 처리 시간(초)"
	)
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

		seatStatusRedisHelper.saveSeatStatus(
			request.performanceScheduleId(),
			request.scheduleSeatId(),
			SeatStatus.AVAILABLE
		);

		seatStatusKafkaHelper.sendSeatStatusChangedEvent(
			request.performanceScheduleId(),
			request.scheduleSeatId(),
			SeatStatus.AVAILABLE
		);

		log.info("[Performance] 좌석 선점 취소 - 성공 - scheduleSeatId={}, scheduleId={}",
			request.scheduleSeatId(), request.performanceScheduleId());

		meterRegistry.counter("performance_seat_lock_fail_total", "reason", "already_sold_out").increment();
		meterRegistry.counter("performance_seat_lock_fail_total", "reason", "redis_fail").increment();

		return new BookingSeatClientResponseDto(null, false, "좌석 선점이 취소되었습니다.");
	}

	@TrackLatency(
		value = "performance_seat_layout_seconds",
		description = "좌석 배치도 조회 API 처리 시간(초)"
	)
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

	@Transactional
	public void updateScheduleSeatStatusByKafka(
		UUID performanceScheduleId, UUID scheduleSeatId, SeatStatus seatStatus) {

		Performance performance = performanceExistenceValidator.validateByPerformanceScheduleId(performanceScheduleId);

		PerformanceSchedule schedule = performance.getScheduleById(performanceScheduleId);

		ScheduleSeat scheduleSeat = schedule.getScheduleSeatById(scheduleSeatId);

		scheduleSeat.changeSeatStatus(seatStatus);

		log.info("[Performance] Kafka 기반 좌석 상태 업데이트 완료 - scheduleSeatId={}, seatStatus={}",
			scheduleSeatId, seatStatus);
	}

	@Transactional(readOnly = true)
	public List<UUID> getAllPerformanceScheduleIds() {

		return performanceQueryRepository.findAllPerformanceScheduleIds();
	}

	@Transactional(readOnly = true)
	public Map<UUID, SeatStatus> getAllSeatStatuses(UUID performanceScheduleId) {

		Performance performance =
			performanceExistenceValidator.validateByPerformanceScheduleId(performanceScheduleId);
		PerformanceSchedule performanceSchedule =
			performance.getScheduleById(performanceScheduleId);

		return performanceSchedule.getScheduleSeats().stream()
			.collect(Collectors.toMap(
				ScheduleSeat::getId,
				ScheduleSeat::getSeatStatus
			));
	}
}
