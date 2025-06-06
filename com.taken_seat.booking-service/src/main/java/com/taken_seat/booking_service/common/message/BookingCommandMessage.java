package com.taken_seat.booking_service.common.message;

import java.time.LocalDateTime;
import java.util.UUID;

public record BookingCommandMessage(
	UUID id,
	UUID userId,
	UUID performanceId,
	UUID performanceScheduleId,
	UUID scheduleSeatId,
	UUID paymentId,
	int price,
	int discountedPrice,
	String bookingStatus,
	LocalDateTime bookedAt,
	LocalDateTime canceledAt,
	LocalDateTime createdAt,
	UUID createdBy,
	LocalDateTime updatedAt,
	UUID updatedBy,
	LocalDateTime deletedAt,
	UUID deletedBy
) {
}