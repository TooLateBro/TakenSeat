package com.taken_seat.booking_service.booking.application.dto.command;

import java.util.UUID;

import org.springframework.data.domain.Pageable;

public record BookingAdminPageReadCommand(
	UUID userId,
	String email,
	String role,
	UUID queryUserId,
	Pageable pageable
) {
}