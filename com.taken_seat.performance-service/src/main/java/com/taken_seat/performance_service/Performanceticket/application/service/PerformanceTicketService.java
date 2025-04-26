package com.taken_seat.performance_service.Performanceticket.application.service;

import org.springframework.stereotype.Service;

import com.taken_seat.common_service.dto.request.TicketPerformanceClientRequest;
import com.taken_seat.common_service.dto.response.TicketPerformanceClientResponse;
import com.taken_seat.performance_service.performance.domain.facade.PerformanceFacade;
import com.taken_seat.performance_service.performance.domain.model.Performance;
import com.taken_seat.performance_service.performance.domain.model.PerformanceSchedule;
import com.taken_seat.performance_service.performance.domain.model.ScheduleSeat;
import com.taken_seat.performance_service.performancehall.domain.facade.PerformanceHallFacade;
import com.taken_seat.performance_service.performancehall.domain.model.PerformanceHall;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PerformanceTicketService {

	public final PerformanceFacade performanceFacade;
	public final PerformanceHallFacade performanceHallFacade;

	public TicketPerformanceClientResponse getPerformanceInfo(TicketPerformanceClientRequest request) {

		Performance performance = performanceFacade.getByPerformanceId(request.performanceId());
		PerformanceSchedule schedule = performance.getScheduleById(request.performanceScheduleId());
		PerformanceHall performanceHall = performanceHallFacade.getByPerformanceHallId(schedule.getPerformanceHallId());
		ScheduleSeat scheduleSeat = schedule.getScheduleSeatById(request.scheduleSeatId());

		return new TicketPerformanceClientResponse(
			performance.getTitle(),
			performanceHall.getName(),
			performanceHall.getAddress(),
			scheduleSeat.getRowNumber(),
			scheduleSeat.getSeatNumber(),
			scheduleSeat.getSeatType().name(),
			schedule.getStartAt(),
			schedule.getEndAt()
		);
	}
}
