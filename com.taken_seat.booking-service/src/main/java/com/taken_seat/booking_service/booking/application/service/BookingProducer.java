package com.taken_seat.booking_service.booking.application.service;

import java.util.UUID;

import com.taken_seat.booking_service.booking.application.dto.event.BookingEntityEvent;
import com.taken_seat.booking_service.common.message.TicketRequestMessage;
import com.taken_seat.common_service.message.BookingCompletedMessage;
import com.taken_seat.common_service.message.PaymentMessage;
import com.taken_seat.common_service.message.PaymentRefundMessage;
import com.taken_seat.common_service.message.QueueEnterMessage;
import com.taken_seat.common_service.message.UserBenefitMessage;

public interface BookingProducer {
	void sendPaymentMessage(PaymentMessage message);

	void sendTicketRequestMessage(TicketRequestMessage message);

	void sendBenefitUsageMessage(UserBenefitMessage message);

	void sendBenefitRefundMessage(UserBenefitMessage message);

	void sendPaymentRefundMessage(PaymentRefundMessage message);

	void sendQueueEnterMessage(QueueEnterMessage message);

	void sendBookingCompletedMessage(BookingCompletedMessage message);

	void sendBookingExpireEvent(UUID bookingId);

	void sendBookingCreatedEvent(BookingEntityEvent event);

	void sendBookingUpdatedEvent(BookingEntityEvent event);
}