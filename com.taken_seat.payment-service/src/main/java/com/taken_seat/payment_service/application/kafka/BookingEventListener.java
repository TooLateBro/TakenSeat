package com.taken_seat.payment_service.application.kafka;

import com.taken_seat.common_service.message.PaymentRequestMessage;

public interface BookingEventListener {

	void sendPaymentRequest(PaymentRequestMessage message);

}
