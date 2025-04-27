package com.taken_seat.payment_service.application.kafka.producer;

import com.taken_seat.common_service.message.PaymentMessage;

public interface PaymentResponseProducer {

	void sendPaymentResponse(PaymentMessage message);

}
