package com.taken_seat.booking_service.booking.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.taken_seat.booking_service.booking.domain.BookingQuery;
import com.taken_seat.booking_service.booking.domain.BookingStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminBookingReadResponse {
	private UUID id;
	private UUID userId;
	private UUID performanceId;
	private UUID performanceScheduleId;
	private UUID scheduleSeatId;
	private UUID paymentId;
	private int price;
	private int discountedPrice;
	private BookingStatus bookingStatus;
	private LocalDateTime bookedAt;
	private LocalDateTime canceledAt;
	private String title;
	private String name;
	private String address;
	private String rowNumber;
	private String seatNumber;
	private String seatType;
	private LocalDateTime createdAt;
	private UUID createdBy;
	private LocalDateTime updatedAt;
	private UUID updatedBy;
	private LocalDateTime deletedAt;
	private UUID deletedBy;

	public static AdminBookingReadResponse toDto(BookingQuery bookingQuery) {
		return AdminBookingReadResponse.builder()
			.id(bookingQuery.getId())
			.userId(bookingQuery.getUserId())
			.performanceId(bookingQuery.getPerformanceId())
			.performanceScheduleId(bookingQuery.getPerformanceScheduleId())
			.scheduleSeatId(bookingQuery.getScheduleSeatId())
			.paymentId(bookingQuery.getPaymentId())
			.price(bookingQuery.getPrice())
			.discountedPrice(bookingQuery.getDiscountedPrice())
			.bookingStatus(bookingQuery.getBookingStatus())
			.bookedAt(bookingQuery.getBookedAt())
			.canceledAt(bookingQuery.getCanceledAt())
			.createdAt(bookingQuery.getCreatedAt())
			.createdBy(bookingQuery.getCreatedBy())
			.updatedAt(bookingQuery.getUpdatedAt())
			.updatedBy(bookingQuery.getUpdatedBy())
			.deletedAt(bookingQuery.getDeletedAt())
			.deletedBy(bookingQuery.getDeletedBy())
			.build();
	}
}