package com.taken_seat.booking_service.ticket.infrastructure.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.taken_seat.common_service.dto.response.TicketPerformanceClientResponse;

@FeignClient(name = "performance-service", path = "/api/v1/performencetickets", contextId = "ticket")
public interface PerformanceClient {

	@GetMapping("/{performanceId}/schedules/{performanceScheduleId}/seats/{seatId}")
	TicketPerformanceClientResponse getPerformanceInfo(
		@PathVariable("performanceId") UUID performanceId,
		@PathVariable("performanceScheduleId") UUID performanceScheduleId,
		@PathVariable("seatId") UUID seatId
	);
}