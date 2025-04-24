package com.taken_seat.performance_service.Performanceticket.application.service;

import org.springframework.stereotype.Service;

import com.taken_seat.common_service.dto.request.TicketPerformanceClientRequest;
import com.taken_seat.common_service.dto.response.TicketPerformanceClientResponse;
import com.taken_seat.performance_service.performance.domain.facade.PerformanceFacade;
import com.taken_seat.performance_service.performance.domain.model.Performance;
import com.taken_seat.performance_service.performance.domain.model.PerformanceSchedule;
import com.taken_seat.performance_service.performancehall.domain.facade.PerformanceHallFacade;
import com.taken_seat.performance_service.performancehall.domain.model.PerformanceHall;
import com.taken_seat.performance_service.performancehall.domain.model.Seat;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PerformanceTicketService {

	public final PerformanceFacade performanceFacade;
	public final PerformanceHallFacade performanceHallFacade;

	public TicketPerformanceClientResponse getPerformanceInfo(TicketPerformanceClientRequest request) {

		Performance performance = performanceFacade.getByPerformanceId(request.getPerformanceId());

		PerformanceSchedule schedule = performance.getScheduleById(request.getPerformanceScheduleId());

		PerformanceHall performanceHall = performanceHallFacade.getByPerformanceHallId(schedule.getPerformanceHallId());

		Seat seat = performanceHall.getSeatById(request.getSeatId());

		return TicketPerformanceClientResponse.builder()
			.title(performance.getTitle())
			.startAt(schedule.getStartAt())
			.endAt(schedule.getEndAt())
			.name(performanceHall.getName())
			.address(performanceHall.getAddress())
			.seatNumber(seat.getSeatNumber())
			.seatRowNumber(seat.getRowNumber())
			.seatType(seat.getSeatType().name())
			.build();
	}
}
