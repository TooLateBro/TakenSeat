package com.taken_seat.performance_service.performenceticket.application.dto.mapper;

import java.util.UUID;

import com.taken_seat.common_service.dto.request.TicketPerformanceClientRequest;

public class RequestMapper {

	public static TicketPerformanceClientRequest fromParams(UUID performanceScheduleId, UUID seatId) {
		return TicketPerformanceClientRequest.builder()
			.performanceScheduleId(performanceScheduleId)
			.seatId(seatId)
			.build();
	}
}
