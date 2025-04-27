package com.taken_seat.booking_service.booking.application.dto.query;

import java.util.UUID;

public record BookingReadQuery(
	UUID userId,
	String email,
	String role,
	UUID bookingId
) {
}