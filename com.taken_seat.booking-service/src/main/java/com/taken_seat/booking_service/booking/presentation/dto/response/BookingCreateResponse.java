package com.taken_seat.booking_service.booking.presentation.dto.response;

import java.util.UUID;

import com.taken_seat.booking_service.booking.domain.BookingCommand;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BookingCreateResponse {
	private UUID bookingId;

	public static BookingCreateResponse toDto(BookingCommand bookingCommand) {
		return new BookingCreateResponse(bookingCommand.getId());
	}
}