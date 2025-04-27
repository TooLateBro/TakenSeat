package com.taken_seat.booking_service.booking.application.service;

import java.util.UUID;

import com.taken_seat.booking_service.booking.application.dto.command.BookingAdminPageReadCommand;
import com.taken_seat.booking_service.booking.application.dto.command.BookingCreateCommand;
import com.taken_seat.booking_service.booking.application.dto.command.BookingPageReadCommand;
import com.taken_seat.booking_service.booking.application.dto.command.BookingPaymentCommand;
import com.taken_seat.booking_service.booking.application.dto.command.BookingSingleTargetCommand;
import com.taken_seat.booking_service.booking.presentation.dto.response.AdminBookingPageResponse;
import com.taken_seat.booking_service.booking.presentation.dto.response.AdminBookingReadResponse;
import com.taken_seat.booking_service.booking.presentation.dto.response.BookingCreateResponse;
import com.taken_seat.booking_service.booking.presentation.dto.response.BookingPageResponse;
import com.taken_seat.booking_service.booking.presentation.dto.response.BookingReadResponse;
import com.taken_seat.common_service.message.BookingRequestMessage;
import com.taken_seat.common_service.message.PaymentMessage;
import com.taken_seat.common_service.message.PaymentRefundMessage;
import com.taken_seat.common_service.message.UserBenefitMessage;

public interface BookingService {
	BookingCreateResponse createBooking(BookingCreateCommand command);

	BookingReadResponse readBooking(BookingSingleTargetCommand command);

	BookingPageResponse readBookings(BookingPageReadCommand command);

	void cancelBooking(BookingSingleTargetCommand command);

	void deleteBooking(BookingSingleTargetCommand command);

	AdminBookingReadResponse adminReadBooking(BookingSingleTargetCommand command);

	AdminBookingPageResponse adminReadBookings(BookingAdminPageReadCommand command);

	void createPayment(BookingPaymentCommand command);

	void updateBooking(PaymentMessage message);

	void createPayment(UserBenefitMessage message);

	void updateBooking(PaymentRefundMessage message);

	void expireBooking(UUID bookingId);

	void updateBenefitUsageHistory(UserBenefitMessage message);

	void acceptFromQueue(BookingRequestMessage message);
}