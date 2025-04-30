package com.taken_seat.booking_service.booking.application.dto.query;

import java.util.UUID;

public record BookingStatusQuery(
	UUID userId,
	UUID performanceId
) {
}