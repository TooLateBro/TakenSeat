package com.taken_seat.booking_service.booking.application.service;

import java.util.UUID;

import com.taken_seat.booking_service.booking.application.dto.command.BookingCreateCommand;
import com.taken_seat.booking_service.booking.application.dto.command.BookingPaymentCommand;
import com.taken_seat.booking_service.booking.application.dto.command.BookingSingleTargetCommand;
import com.taken_seat.booking_service.booking.presentation.dto.response.BookingCreateResponse;

public interface BookingCommandService {
	BookingCreateResponse createBooking(BookingCreateCommand command);

	void cancelBooking(BookingSingleTargetCommand command);

	void deleteBooking(BookingSingleTargetCommand command);

	void createPayment(BookingPaymentCommand command);

	void expireBooking(UUID bookingId);
}