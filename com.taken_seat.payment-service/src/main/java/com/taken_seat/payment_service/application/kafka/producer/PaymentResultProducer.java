package com.taken_seat.payment_service.application.kafka.producer;

import com.taken_seat.common_service.message.PaymentMessage;

public interface PaymentResultProducer {

	void sendPaymentResult(PaymentMessage message);

}
