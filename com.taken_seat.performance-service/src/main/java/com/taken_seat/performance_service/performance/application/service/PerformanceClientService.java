package com.taken_seat.performance_service.performance.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taken_seat.common_service.dto.request.BookingSeatClientRequestDto;
import com.taken_seat.common_service.dto.response.BookingSeatClientResponseDto;
import com.taken_seat.common_service.dto.response.PerformanceEndTimeDto;
import com.taken_seat.common_service.dto.response.PerformanceStartTimeDto;
import com.taken_seat.common_service.exception.customException.PerformanceException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
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
	private final PerformanceResponseMapper performanceResponseMapper;

	@Transactional
	public BookingSeatClientResponseDto updateSeatStatus(BookingSeatClientRequestDto request) {

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

	@Transactional
	public PerformanceEndTimeDto getPerformanceEndTime(UUID performanceId, UUID performanceScheduleId) {

		Performance performance = performanceExistenceValidator.validateByPerformanceId(performanceId);

		PerformanceSchedule schedule = performance.getScheduleById(performanceScheduleId);

		return new PerformanceEndTimeDto(schedule.getEndAt());
	}

	@Transactional
	public PerformanceStartTimeDto getPerformanceStartTime(UUID performanceId, UUID performanceScheduleId) {

		Performance performance = performanceExistenceValidator.validateByPerformanceId(performanceId);

		PerformanceSchedule schedule = performance.getScheduleById(performanceScheduleId);

		return new PerformanceStartTimeDto(schedule.getStartAt());
	}
}
