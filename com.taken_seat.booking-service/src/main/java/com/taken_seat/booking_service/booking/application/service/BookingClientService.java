package com.taken_seat.booking_service.booking.application.service;

import java.util.UUID;

import com.taken_seat.common_service.dto.request.BookingSeatClientRequestDto;
import com.taken_seat.common_service.dto.response.BookingSeatClientResponseDto;
import com.taken_seat.common_service.dto.response.BookingStatusDto;

public interface BookingClientService {
	BookingStatusDto getBookingStatus(UUID userId, UUID performanceId);

	BookingSeatClientResponseDto updateSeatStatus(BookingSeatClientRequestDto request);

	BookingSeatClientResponseDto cancelSeatStatus(BookingSeatClientRequestDto requestDto);
}