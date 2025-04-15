package com.taken_seat.booking_service.booking.application.service;

import com.taken_seat.booking_service.common.message.TicketRequestMessage;

public interface BookingProducer {
	void sendPaymentRequestEvent(PaymentRequestMessage message);

	void sendPaymentCompleteEvent(TicketRequestMessage message);
}