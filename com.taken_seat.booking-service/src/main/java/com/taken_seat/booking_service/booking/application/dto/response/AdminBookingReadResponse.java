package com.taken_seat.booking_service.booking.application.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.taken_seat.booking_service.booking.domain.Booking;
import com.taken_seat.booking_service.booking.domain.BookingStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminBookingReadResponse {
	private UUID id;
	private UUID userId;
	private UUID performanceScheduleId;
	private UUID seatId;
	private UUID paymentId;
	private BookingStatus bookingStatus;
	private LocalDateTime bookedAt;
	private LocalDateTime canceledAt;
	private LocalDateTime createdAt;
	private UUID createdBy;
	private LocalDateTime updatedAt;
	private UUID updatedBy;
	private LocalDateTime deletedAt;
	private UUID deletedBy;

	public static AdminBookingReadResponse toDto(Booking booking) {
		return AdminBookingReadResponse.builder()
			.id(booking.getId())
			.userId(booking.getUserId())
			.performanceScheduleId(booking.getPerformanceScheduleId())
			.seatId(booking.getSeatId())
			.paymentId(booking.getPaymentId())
			.bookingStatus(booking.getBookingStatus())
			.bookedAt(booking.getBookedAt())
			.canceledAt(booking.getCanceledAt())
			.createdAt(booking.getCreatedAt())
			.createdBy(booking.getCreatedBy())
			.updatedAt(booking.getUpdatedAt())
			.updatedBy(booking.getUpdatedBy())
			.deletedAt(booking.getDeletedAt())
			.deletedBy(booking.getDeletedBy())
			.build();
	}
}