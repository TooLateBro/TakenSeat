package com.taken_seat.booking_service.booking.application.dto.command;

import java.util.UUID;

public record BookingSingleTargetCommand(
	UUID userId,
	String email,
	String role,
	UUID bookingId
) {
}