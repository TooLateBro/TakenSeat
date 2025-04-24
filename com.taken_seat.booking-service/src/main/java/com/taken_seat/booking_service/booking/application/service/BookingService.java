package com.taken_seat.booking_service.booking.application.service;

import java.util.UUID;

import org.springframework.data.domain.Pageable;

import com.taken_seat.booking_service.booking.application.dto.request.BookingCreateRequest;
import com.taken_seat.booking_service.booking.application.dto.request.BookingPayRequest;
import com.taken_seat.booking_service.booking.application.dto.response.AdminBookingPageResponse;
import com.taken_seat.booking_service.booking.application.dto.response.AdminBookingReadResponse;
import com.taken_seat.booking_service.booking.application.dto.response.BookingCreateResponse;
import com.taken_seat.booking_service.booking.application.dto.response.BookingPageResponse;
import com.taken_seat.booking_service.booking.application.dto.response.BookingReadResponse;
import com.taken_seat.common_service.dto.AuthenticatedUser;
import com.taken_seat.common_service.message.BookingRequestMessage;
import com.taken_seat.common_service.message.PaymentMessage;
import com.taken_seat.common_service.message.PaymentRefundMessage;
import com.taken_seat.common_service.message.UserBenefitMessage;

public interface BookingService {
	BookingCreateResponse createBooking(AuthenticatedUser authenticatedUser, BookingCreateRequest request);

	BookingReadResponse readBooking(AuthenticatedUser authenticatedUser, UUID id);

	BookingPageResponse readBookings(AuthenticatedUser authenticatedUser, Pageable pageable);

	void cancelBooking(AuthenticatedUser authenticatedUser, UUID id);

	void deleteBooking(AuthenticatedUser authenticatedUser, UUID id);

	AdminBookingReadResponse adminReadBooking(AuthenticatedUser authenticatedUser, UUID id);

	AdminBookingPageResponse adminReadBookings(AuthenticatedUser authenticatedUser, UUID userId, Pageable pageable);

	void createPayment(AuthenticatedUser authenticatedUser, UUID id, BookingPayRequest request);

	void updateBooking(PaymentMessage message);

	void createPayment(UserBenefitMessage message);

	void updateBooking(PaymentRefundMessage message);

	void expireBooking(UUID bookingId);

	void updateBenefitUsageHistory(UserBenefitMessage message);

	void acceptFromQueue(BookingRequestMessage message);
}