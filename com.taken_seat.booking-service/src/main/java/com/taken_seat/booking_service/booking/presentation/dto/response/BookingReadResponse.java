package com.taken_seat.booking_service.booking.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.taken_seat.booking_service.booking.domain.BookingQuery;
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
	private LocalDateTime startAt;
	private LocalDateTime endAt;

	public static BookingReadResponse toDto(BookingQuery bookingQuery) {
		return BookingReadResponse.builder()
			.id(bookingQuery.getId())
			.performanceId(bookingQuery.getPerformanceId())
			.performanceScheduleId(bookingQuery.getPerformanceScheduleId())
			.scheduleSeatId(bookingQuery.getScheduleSeatId())
			.paymentId(bookingQuery.getPaymentId())
			.price(bookingQuery.getPrice())
			.discountedPrice(bookingQuery.getDiscountedPrice())
			.bookingStatus(bookingQuery.getBookingStatus())
			.bookedAt(bookingQuery.getBookedAt())
			.canceledAt(bookingQuery.getCanceledAt())
			.title(bookingQuery.getTitle())
			.name(bookingQuery.getName())
			.address(bookingQuery.getAddress())
			.rowNumber(bookingQuery.getRowNumber())
			.seatNumber(bookingQuery.getSeatNumber())
			.seatType(bookingQuery.getSeatType())
			.startAt(bookingQuery.getStartAt())
			.endAt(bookingQuery.getEndAt())
			.build();
	}
}