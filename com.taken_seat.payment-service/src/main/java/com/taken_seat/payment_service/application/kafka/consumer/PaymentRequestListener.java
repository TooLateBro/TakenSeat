package com.taken_seat.payment_service.application.kafka.consumer;

import com.taken_seat.common_service.message.PaymentMessage;

public interface PaymentRequestListener {

	void handlePaymentRequest(PaymentMessage message);

}
