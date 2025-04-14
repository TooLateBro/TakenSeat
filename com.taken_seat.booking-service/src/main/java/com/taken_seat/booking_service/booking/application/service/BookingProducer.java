package com.taken_seat.booking_service.booking.application.service;

import com.taken_seat.booking_service.common.message.TicketRequestMessage;
import com.taken_seat.common_service.message.PaymentRequestMessage;

public interface BookingProducer {
	void sendPaymentRequestEvent(PaymentRequestMessage message);

	void sendPaymentCompleteEvent(TicketRequestMessage message);
}