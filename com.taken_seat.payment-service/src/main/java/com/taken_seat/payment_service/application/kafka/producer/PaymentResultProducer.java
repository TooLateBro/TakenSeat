package com.taken_seat.payment_service.application.kafka.producer;

import com.taken_seat.common_service.message.PaymentResultMessage;

public interface PaymentResultProducer {

	void sendPaymentResult(PaymentResultMessage message);

}
