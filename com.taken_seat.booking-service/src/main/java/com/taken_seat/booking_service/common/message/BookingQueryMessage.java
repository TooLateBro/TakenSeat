package com.taken_seat.booking_service.common.message;

import java.time.LocalDateTime;
import java.util.UUID;

public record BookingQueryMessage(
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
	String title,
	String name,
	String address,
	String rowNumber,
	String seatNumber,
	String seatType,
	LocalDateTime startAt,
	LocalDateTime endAt,
	LocalDateTime createdAt,
	UUID createdBy,
	LocalDateTime updatedAt,
	UUID updatedBy,
	LocalDateTime deletedAt,
	UUID deletedBy
) {
}