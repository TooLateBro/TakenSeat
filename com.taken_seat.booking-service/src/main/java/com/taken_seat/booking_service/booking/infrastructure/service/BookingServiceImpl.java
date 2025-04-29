package com.taken_seat.booking_service.booking.infrastructure.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.taken_seat.booking_service.booking.application.dto.command.BookingCreateCommand;
import com.taken_seat.booking_service.booking.application.dto.command.BookingPaymentCommand;
import com.taken_seat.booking_service.booking.application.dto.command.BookingSingleTargetCommand;
import com.taken_seat.booking_service.booking.application.dto.event.BookingEntityEvent;
import com.taken_seat.booking_service.booking.application.dto.query.BookingAdminListQuery;
import com.taken_seat.booking_service.booking.application.dto.query.BookingListQuery;
import com.taken_seat.booking_service.booking.application.dto.query.BookingReadQuery;
import com.taken_seat.booking_service.booking.application.service.BookingService;
import com.taken_seat.booking_service.booking.presentation.dto.response.AdminBookingPageResponse;
import com.taken_seat.booking_service.booking.presentation.dto.response.AdminBookingReadResponse;
import com.taken_seat.booking_service.booking.presentation.dto.response.BookingCreateResponse;
import com.taken_seat.booking_service.booking.presentation.dto.response.BookingPageResponse;
import com.taken_seat.booking_service.booking.presentation.dto.response.BookingReadResponse;
import com.taken_seat.common_service.message.BookingRequestMessage;
import com.taken_seat.common_service.message.PaymentMessage;
import com.taken_seat.common_service.message.PaymentRefundMessage;
import com.taken_seat.common_service.message.UserBenefitMessage;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

	private final BookingCommandService bookingCommandService;
	private final BookingQueryService bookingQueryService;

	@Override
	public BookingCreateResponse createBooking(BookingCreateCommand command) {
		return bookingCommandService.createBooking(command);
	}

	@Override
	public void cancelBooking(BookingSingleTargetCommand command) {
		bookingCommandService.cancelBooking(command);
	}

	@Override
	public void deleteBooking(BookingSingleTargetCommand command) {
		bookingCommandService.deleteBooking(command);
	}

	@Override
	public void createPayment(BookingPaymentCommand command) {
		bookingCommandService.createPayment(command);
	}

	@Override
	public void receiveBookingExpireEvent(UUID bookingId) {
		bookingCommandService.receiveBookingExpireEvent(bookingId);
	}

	@Override
	public void receiveBenefitUsageMessage(UserBenefitMessage message) {
		bookingCommandService.receiveBenefitUsageMessage(message);
	}

	@Override
	public void receivePaymentMessage(PaymentMessage message) {
		bookingCommandService.receivePaymentMessage(message);
	}

	@Override
	public void receivePaymentRefundMessage(PaymentRefundMessage message) {
		bookingCommandService.receivePaymentRefundMessage(message);
	}

	@Override
	public void receiveBenefitRefundMessage(UserBenefitMessage message) {
		bookingCommandService.receiveBenefitRefundMessage(message);
	}

	@Override
	public void receiveWaitingQueueMessage(BookingRequestMessage message) {
		bookingCommandService.receiveWaitingQueueMessage(message);
	}

	@Override
	public BookingReadResponse readBooking(BookingReadQuery query) {
		return bookingQueryService.readBooking(query);
	}

	@Override
	public BookingPageResponse readBookings(BookingListQuery query) {
		return bookingQueryService.readBookings(query);
	}

	@Override
	public AdminBookingReadResponse adminReadBooking(BookingReadQuery query) {
		return bookingQueryService.adminReadBooking(query);
	}

	@Override
	public AdminBookingPageResponse adminReadBookings(BookingAdminListQuery query) {
		return bookingQueryService.adminReadBookings(query);
	}

	@Override
	public void receiveBookingCreatedEvent(BookingEntityEvent event) {
		bookingQueryService.receiveBookingCreatedEvent(event);
	}

	@Override
	public void receiveBookingUpdatedEvent(BookingEntityEvent event) {
		bookingQueryService.receiveBookingUpdatedEvent(event);
	}
}