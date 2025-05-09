package com.taken_seat.booking_service.booking.application.service;

import java.util.UUID;

import com.taken_seat.booking_service.common.message.BookingCommandMessage;
import com.taken_seat.booking_service.common.message.BookingPaymentRequestMessage;
import com.taken_seat.booking_service.common.message.BookingQueryMessage;
import com.taken_seat.common_service.message.BookingCompletedMessage;
import com.taken_seat.common_service.message.PaymentMessage;
import com.taken_seat.common_service.message.PaymentRefundMessage;
import com.taken_seat.common_service.message.QueueEnterMessage;
import com.taken_seat.common_service.message.UserBenefitMessage;

public interface BookingProducer {
	void sendPaymentMessage(PaymentMessage message);

	void sendPaymentRequestMessage(BookingPaymentRequestMessage message);

	void sendTicketRequestMessage(BookingQueryMessage message);

	void sendBenefitUsageMessage(UserBenefitMessage message);

	void sendBenefitRefundMessage(UserBenefitMessage message);

	void sendPaymentRefundMessage(PaymentRefundMessage message);

	void sendQueueEnterMessage(QueueEnterMessage message);

	void sendBookingCompletedMessage(BookingCompletedMessage message);

	void sendBookingCompletedMessage(UUID bookingId);

	void sendBookingExpireEvent(UUID bookingId);

	void sendBookingCreatedEvent(BookingCommandMessage event);

	void sendBookingUpdatedEvent(BookingCommandMessage event);
}