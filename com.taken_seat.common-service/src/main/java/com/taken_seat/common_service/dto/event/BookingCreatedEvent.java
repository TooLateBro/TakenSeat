package com.taken_seat.common_service.dto.event;

import java.util.UUID;

public record BookingCreatedEvent(
	UUID bookingId,
	UUID userId,
	UUID performanceId,
	UUID performanceScheduleId,
	Integer price
) {
}