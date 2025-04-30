package com.taken_seat.booking_service.booking.infrastructure.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.taken_seat.booking_service.booking.application.service.BookingClientService;
import com.taken_seat.booking_service.booking.presentation.dto.response.SeatLayoutResponseDto;
import com.taken_seat.booking_service.common.client.PerformanceClient;
import com.taken_seat.common_service.aop.TrackLatency;
import com.taken_seat.common_service.dto.ApiResponseData;
import com.taken_seat.common_service.dto.request.BookingSeatClientRequestDto;
import com.taken_seat.common_service.dto.response.BookingSeatClientResponseDto;
import com.taken_seat.common_service.dto.response.PerformanceStartTimeDto;
import com.taken_seat.common_service.dto.response.TicketPerformanceClientResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingClientServiceImpl implements BookingClientService {

	private final PerformanceClient performanceClient;

	@Override
	public BookingSeatClientResponseDto updateSeatStatus(BookingSeatClientRequestDto request) {
		ApiResponseData<BookingSeatClientResponseDto> response = performanceClient.updateSeatStatus(request);

		return response.body();
	}

	@Override
	public BookingSeatClientResponseDto cancelSeatStatus(BookingSeatClientRequestDto requestDto) {
		ApiResponseData<BookingSeatClientResponseDto> response = performanceClient.cancelSeatStatus(requestDto);

		return response.body();
	}

	@Override
	public PerformanceStartTimeDto getPerformanceStartTime(UUID performanceId, UUID performanceScheduleId) {
		ApiResponseData<PerformanceStartTimeDto> response = performanceClient.getPerformanceStartTime(performanceId,
			performanceScheduleId);

		return response.body();
	}

	@TrackLatency(
		value = "booking_client_get_seat_layout_seconds",
		description = "예매 클라이언트 - 좌석 배치도 조회 처리 시간(초)"
	)
	@Override
	public SeatLayoutResponseDto getSeatLayout(UUID performanceScheduleId) {
		ApiResponseData<SeatLayoutResponseDto> response = performanceClient.getSeatLayout(performanceScheduleId);

		return response.body();
	}

	@Override
	public TicketPerformanceClientResponse getPerformanceInfo(UUID performanceId, UUID performanceScheduleId,
		UUID scheduleSeatId) {
		ApiResponseData<TicketPerformanceClientResponse> response = performanceClient.getPerformanceInfo(performanceId,
			performanceScheduleId, scheduleSeatId);

		return response.body();
	}
}