package com.taken_seat.performance_service.performanceticket.application.dto.mapper;

import java.util.UUID;

import com.taken_seat.common_service.dto.request.TicketPerformanceClientRequest;

public class RequestMapper {

	public static TicketPerformanceClientRequest fromParams(
		UUID performanceId,
		UUID performanceScheduleId,
		UUID scheduleSeatId) {
		return new TicketPerformanceClientRequest(
			performanceId,
			performanceScheduleId,
			scheduleSeatId
		);
	}
}
