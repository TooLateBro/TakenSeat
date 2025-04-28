package com.taken_seat.booking_service.booking.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.taken_seat.booking_service.booking.domain.Booking;
import com.taken_seat.booking_service.booking.domain.BookingStatus;
import com.taken_seat.common_service.dto.response.TicketPerformanceClientResponse;

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
	private String seatRowNumber;
	private String seatNumber;
	private String seatType;

	public static BookingReadResponse toDto(Booking booking, TicketPerformanceClientResponse response) {
		return BookingReadResponse.builder()
			.id(booking.getId())
			.performanceId(booking.getPerformanceId())
			.performanceScheduleId(booking.getPerformanceScheduleId())
			.scheduleSeatId(booking.getScheduleSeatId())
			.paymentId(booking.getPaymentId())
			.price(booking.getPrice())
			.discountedPrice(booking.getDiscountedPrice())
			.bookingStatus(booking.getBookingStatus())
			.bookedAt(booking.getBookedAt())
			.canceledAt(booking.getCanceledAt())
			.title(response.title())
			.name(response.name())
			.address(response.address())
			.seatRowNumber(response.rowNumber())
			.seatNumber(response.seatNumber())
			.seatType(response.seatType())
			.build();
	}
}