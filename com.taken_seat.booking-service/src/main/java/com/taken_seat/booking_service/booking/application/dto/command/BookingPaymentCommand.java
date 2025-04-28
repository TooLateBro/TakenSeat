package com.taken_seat.booking_service.booking.application.dto.command;

import java.util.UUID;

public record BookingPaymentCommand(
	UUID userId,
	String email,
	String role,
	UUID bookingId,
	UUID couponId,
	Integer mileage
) {
}