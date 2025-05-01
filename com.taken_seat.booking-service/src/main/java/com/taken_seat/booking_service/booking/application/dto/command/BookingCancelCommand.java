package com.taken_seat.booking_service.booking.application.dto.command;

import java.util.UUID;

public record BookingCancelCommand(
	UUID userId,
	String email,
	String role,
	UUID bookingId,
	String cancelReason
) {
}