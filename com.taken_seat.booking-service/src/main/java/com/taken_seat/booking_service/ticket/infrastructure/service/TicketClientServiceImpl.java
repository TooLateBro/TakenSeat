package com.taken_seat.booking_service.ticket.infrastructure.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.taken_seat.booking_service.common.client.PerformanceClient;
import com.taken_seat.booking_service.ticket.application.service.TicketClientService;
import com.taken_seat.common_service.dto.ApiResponseData;
import com.taken_seat.common_service.dto.response.TicketPerformanceClientResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketClientServiceImpl implements TicketClientService {

	private final PerformanceClient performanceClient;

	@Override
	public TicketPerformanceClientResponse getPerformanceInfo(UUID performanceId, UUID performanceScheduleId,
		UUID seatId) {
		ApiResponseData<TicketPerformanceClientResponse> response = performanceClient.getPerformanceInfo(
			performanceId, performanceScheduleId, seatId);

		return response.body();
	}
}