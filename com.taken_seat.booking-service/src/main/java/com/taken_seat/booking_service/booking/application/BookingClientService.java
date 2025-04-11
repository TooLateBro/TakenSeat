package com.taken_seat.booking_service.booking.application;

import java.util.UUID;

import com.taken_seat.common_service.dto.response.BookingStatusDto;

public interface BookingClientService {
	BookingStatusDto getBookingStatus(UUID userId, UUID performanceId);
}