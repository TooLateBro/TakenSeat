package com.taken_seat.payment_service.application.kafka.producer;

import com.taken_seat.common_service.message.PaymentRefundMessage;

public interface PaymentRefundResultProducer {

	void sendPaymentRefundResult(PaymentRefundMessage message);
}
