package com.taken_seat.booking_service.application.dto.response;

import java.util.UUID;

import com.taken_seat.booking_service.domain.Booking;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BookingCreateResponse {
	private UUID bookingId;

	public static BookingCreateResponse toDto(Booking booking) {
		return new BookingCreateResponse(booking.getId());
	}
}