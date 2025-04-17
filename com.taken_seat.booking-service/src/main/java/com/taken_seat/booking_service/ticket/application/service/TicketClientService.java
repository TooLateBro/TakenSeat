package com.taken_seat.booking_service.ticket.application.service;

import java.util.UUID;

import com.taken_seat.common_service.dto.response.TicketPerformanceClientResponse;

public interface TicketClientService {
	TicketPerformanceClientResponse getPerformanceInfo(UUID performanceId, UUID performanceScheduleId,
		UUID seatId);
}