package com.taken_seat.performance_service.performancehall.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taken_seat.common_service.dto.request.BookingSeatClientRequestDto;
import com.taken_seat.common_service.dto.response.BookingSeatClientResponseDto;
import com.taken_seat.common_service.exception.customException.PerformanceException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import com.taken_seat.performance_service.performance.domain.facade.PerformanceFacade;
import com.taken_seat.performance_service.performance.domain.model.Performance;
import com.taken_seat.performance_service.performance.domain.model.PerformanceSchedule;
import com.taken_seat.performance_service.performancehall.application.dto.mapper.HallResponseMapper;
import com.taken_seat.performance_service.performancehall.application.event.SeatStatusEventPublisher;
import com.taken_seat.performance_service.performancehall.domain.model.PerformanceHall;
import com.taken_seat.performance_service.performancehall.domain.model.Seat;
import com.taken_seat.performance_service.performancehall.domain.model.SeatStatus;
import com.taken_seat.performance_service.performancehall.domain.repository.PerformanceHallRepository;
import com.taken_seat.performance_service.performancehall.domain.validation.PerformanceHallExistenceValidator;
import com.taken_seat.performance_service.performancehall.presentation.dto.response.SeatDto;
import com.taken_seat.performance_service.performancehall.presentation.dto.response.SeatLayoutResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PerformanceHallClientService {

	private final PerformanceFacade performanceFacade;
	private final PerformanceHallExistenceValidator performanceHallExistenceValidator;
	private final PerformanceHallRepository performanceHallRepository;
	private final SeatStatusEventPublisher seatStatusEventPublisher;

	@Transactional
	public BookingSeatClientResponseDto updateSeatStatus(BookingSeatClientRequestDto request) {

		PerformanceHall performanceHall =
			performanceHallExistenceValidator.validateBySeatId(request.getSeatId());

		Seat seat = performanceHall.getSeatById(request.getSeatId());

		if (seat.getStatus() == SeatStatus.SOLDOUT) {
			log.warn("[Performance] 좌석 선점 - 실패 - seatId={}, scheduleId={}, 이유=이미 선점됨",
				request.getSeatId(), request.getPerformanceScheduleId());
			return new BookingSeatClientResponseDto(null, false, "이미 선점된 좌석입니다.");
		}

		seat.updateStatus(SeatStatus.SOLDOUT);

		performanceHallRepository.saveAndFlush(performanceHall);

		seatStatusEventPublisher.publish(
			request.getPerformanceId(),
			request.getPerformanceScheduleId(),
			request.getSeatId(),
			SeatStatus.SOLDOUT
		);

		Performance performance = performanceFacade.getByPerformanceId(request.getPerformanceId());

		Integer price = performance.findPriceByScheduleAndSeatType(
			request.getPerformanceScheduleId(),
			seat.getSeatType()
		);

		log.info(
			"[Performance] 좌석 선점 - 성공 - seatId={}, scheduleId={}, performanceId={}, performanceHallId={}, seatType={}, price={}",
			request.getSeatId(), request.getPerformanceScheduleId(), performanceHall.getId(),
			request.getPerformanceId(), seat.getSeatType(), price);

		return new BookingSeatClientResponseDto(price, true, "좌석 선점에 성공했습니다.");
	}

	@Transactional
	public BookingSeatClientResponseDto cancelSeatStatus(BookingSeatClientRequestDto request) {

		PerformanceHall performanceHall =
			performanceHallExistenceValidator.validateBySeatId(request.getSeatId());

		Seat seat = performanceHall.getSeatById(request.getSeatId());

		if (seat.getStatus() == SeatStatus.DISABLED) {
			log.warn("[Performance] 좌석 선점 취소 - 실패 - seatId={}, 이유=변경 불가 상태(DISABLED)",
				request.getSeatId());
			throw new PerformanceException(ResponseCode.SEAT_STATUS_CHANGE_NOT_ALLOWED);
		}

		seat.updateStatus(SeatStatus.AVAILABLE);

		seatStatusEventPublisher.publish(
			request.getPerformanceId(),
			request.getPerformanceScheduleId(),
			request.getSeatId(),
			SeatStatus.AVAILABLE
		);

		log.info("[Performance] 좌석 선점 취소 - 성공 - seatId={}, scheduleId={}",
			request.getSeatId(), request.getPerformanceScheduleId());

		return new BookingSeatClientResponseDto(null, false, "좌석 선점이 취소되었습니다.");
	}

	@Transactional(readOnly = true)
	public SeatLayoutResponseDto getSeatLayout(UUID performanceScheduleId) {

		Performance performance = performanceFacade.getByPerformanceScheduleId(performanceScheduleId);
		PerformanceSchedule schedule = performance.getScheduleById(performanceScheduleId);

		UUID performanceHallId = schedule.getPerformanceHallId();

		PerformanceHall performanceHall =
			performanceHallExistenceValidator.validateByPerformanceHallId(performanceHallId);

		log.info("[Performance] 좌석 배치도 조회 - scheduleId={}, hallId={}, 좌석 수={}",
			performanceScheduleId, performanceHallId, performanceHall.getSeats().size());

		List<SeatDto> seatLayout = HallResponseMapper.toSeatLayout(performanceHall.getSeats());

		return new SeatLayoutResponseDto(seatLayout);
	}
}
