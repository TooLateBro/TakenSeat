package com.taken_seat.booking_service.booking.application.service;

import java.util.UUID;

import com.taken_seat.booking_service.booking.presentation.dto.response.SeatLayoutResponseDto;
import com.taken_seat.common_service.dto.request.BookingSeatClientRequestDto;
import com.taken_seat.common_service.dto.response.BookingSeatClientResponseDto;
import com.taken_seat.common_service.dto.response.BookingStatusDto;
import com.taken_seat.common_service.dto.response.PerformanceStartTimeDto;
import com.taken_seat.common_service.dto.response.TicketPerformanceClientResponse;

public interface BookingClientService {
	BookingStatusDto getBookingStatus(UUID userId, UUID performanceId);

	BookingSeatClientResponseDto updateSeatStatus(BookingSeatClientRequestDto request);

	BookingSeatClientResponseDto cancelSeatStatus(BookingSeatClientRequestDto requestDto);

	PerformanceStartTimeDto getPerformanceStartTime(UUID performanceId, UUID performanceScheduleId);

	SeatLayoutResponseDto getSeatLayout(UUID performanceScheduleId);

	TicketPerformanceClientResponse getPerformanceInfo(UUID performanceId, UUID performanceScheduleId, UUID seatId);
}