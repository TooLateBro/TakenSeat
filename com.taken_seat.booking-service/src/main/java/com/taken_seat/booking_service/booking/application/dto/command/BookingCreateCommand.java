package com.taken_seat.booking_service.booking.application.dto.command;

import java.util.UUID;

public record BookingCreateCommand(
	UUID userId,
	String email,
	String role,
	UUID performanceId,
	UUID performanceScheduleId,
	UUID scheduleSeatId
) {
}