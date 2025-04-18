package com.taken_seat.booking_service.booking.application.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.taken_seat.booking_service.booking.domain.Booking;
import com.taken_seat.booking_service.booking.domain.BookingStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor
public class BookingReadResponse {
	private UUID id;
	private UUID performanceId;
	private UUID performanceScheduleId;
	private UUID seatId;
	private UUID paymentId;
	private int price;
	private int discountedPrice;
	private BookingStatus bookingStatus;
	private LocalDateTime bookedAt;
	private LocalDateTime canceledAt;

	public static BookingReadResponse toDto(Booking booking) {
		return BookingReadResponse.builder()
			.id(booking.getId())
			.performanceId(booking.getPerformanceId())
			.performanceScheduleId(booking.getPerformanceScheduleId())
			.seatId(booking.getSeatId())
			.paymentId(booking.getPaymentId())
			.price(booking.getPrice())
			.discountedPrice(booking.getDiscountedPrice())
			.bookingStatus(booking.getBookingStatus())
			.bookedAt(booking.getBookedAt())
			.canceledAt(booking.getCanceledAt())
			.build();
	}
}