package com.taken_seat.booking_service.booking.application.dto.query;

import java.util.UUID;

import org.springframework.data.domain.Pageable;

public record BookingListQuery(
	UUID userId,
	String email,
	String role,
	Pageable pageable
) {
}