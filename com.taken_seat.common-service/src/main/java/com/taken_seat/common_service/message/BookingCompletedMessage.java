package com.taken_seat.common_service.message;

import java.util.UUID;

public record BookingCompletedMessage(
	UUID userId,
	UUID performanceId,
	int ticketCount
) {
}