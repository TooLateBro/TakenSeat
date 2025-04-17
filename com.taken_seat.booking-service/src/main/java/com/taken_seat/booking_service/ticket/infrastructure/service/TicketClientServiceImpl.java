package com.taken_seat.booking_service.ticket.infrastructure.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.taken_seat.booking_service.ticket.application.service.TicketClientService;
import com.taken_seat.booking_service.ticket.infrastructure.client.PerformanceClient;
import com.taken_seat.common_service.dto.response.TicketPerformanceClientResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketClientServiceImpl implements TicketClientService {

	private final PerformanceClient performanceClient;

	@Override
	public TicketPerformanceClientResponse getPerformanceInfo(UUID performanceId, UUID performanceScheduleId,
		UUID seatId) {

		return performanceClient.getPerformanceInfo(performanceId, performanceScheduleId, seatId);
	}
}