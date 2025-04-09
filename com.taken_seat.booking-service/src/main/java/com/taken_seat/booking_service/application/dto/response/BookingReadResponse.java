package com.taken_seat.booking_service.application.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.taken_seat.booking_service.domain.Booking;
import com.taken_seat.booking_service.domain.BookingStatus;

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
	private UUID performanceScheduleId;
	private UUID seatId;
	private UUID paymentId;
	private BookingStatus bookingStatus;
	private LocalDateTime bookedAt;
	private LocalDateTime canceledAt;

	public static BookingReadResponse toDto(Booking booking) {
		return BookingReadResponse.builder()
			.id(booking.getId())
			.performanceScheduleId(booking.getPerformanceScheduleId())
			.seatId(booking.getSeatId())
			.paymentId(booking.getPaymentId())
			.bookingStatus(booking.getBookingStatus())
			.bookedAt(booking.getBookedAt())
			.canceledAt(booking.getCanceledAt())
			.build();
	}
}