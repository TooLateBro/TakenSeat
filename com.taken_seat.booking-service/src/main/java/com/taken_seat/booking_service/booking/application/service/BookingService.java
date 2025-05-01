package com.taken_seat.booking_service.booking.application.service;

import java.util.UUID;

import com.taken_seat.booking_service.booking.application.dto.command.BookingCancelCommand;
import com.taken_seat.booking_service.booking.application.dto.command.BookingCreateCommand;
import com.taken_seat.booking_service.booking.application.dto.command.BookingPaymentCommand;
import com.taken_seat.booking_service.booking.application.dto.command.BookingSingleTargetCommand;
import com.taken_seat.booking_service.booking.application.dto.event.BookingEntityEvent;
import com.taken_seat.booking_service.booking.application.dto.query.BookingAdminListQuery;
import com.taken_seat.booking_service.booking.application.dto.query.BookingListQuery;
import com.taken_seat.booking_service.booking.application.dto.query.BookingReadQuery;
import com.taken_seat.booking_service.booking.application.dto.query.BookingStatusQuery;
import com.taken_seat.booking_service.booking.presentation.dto.response.AdminBookingPageResponse;
import com.taken_seat.booking_service.booking.presentation.dto.response.AdminBookingReadResponse;
import com.taken_seat.booking_service.booking.presentation.dto.response.BookingCreateResponse;
import com.taken_seat.booking_service.booking.presentation.dto.response.BookingPageResponse;
import com.taken_seat.booking_service.booking.presentation.dto.response.BookingReadResponse;
import com.taken_seat.common_service.dto.response.BookingStatusDto;
import com.taken_seat.common_service.message.BookingRequestMessage;
import com.taken_seat.common_service.message.PaymentMessage;
import com.taken_seat.common_service.message.PaymentRefundMessage;
import com.taken_seat.common_service.message.UserBenefitMessage;

public interface BookingService {
	BookingCreateResponse createBooking(BookingCreateCommand command);

	void cancelBooking(BookingCancelCommand command);

	void deleteBooking(BookingSingleTargetCommand command);

	void createPayment(BookingPaymentCommand command);

	void receiveBookingExpireEvent(UUID bookingId);

	void receiveBenefitUsageMessage(UserBenefitMessage message);

	void receivePaymentMessage(PaymentMessage message);

	void receivePaymentRefundMessage(PaymentRefundMessage message);

	void receiveBenefitRefundMessage(UserBenefitMessage message);

	void receiveWaitingQueueMessage(BookingRequestMessage message);

	BookingReadResponse readBooking(BookingReadQuery query);

	BookingPageResponse readBookings(BookingListQuery query);

	AdminBookingReadResponse adminReadBooking(BookingReadQuery query);

	AdminBookingPageResponse adminReadBookings(BookingAdminListQuery query);

	BookingStatusDto getBookingStatus(BookingStatusQuery query);

	void receiveBookingCreatedEvent(BookingEntityEvent event);

	void receiveBookingUpdatedEvent(BookingEntityEvent event);

	void reissueTicket(BookingSingleTargetCommand command);
}