package com.taken_seat.payment_service.application.kafka.consumer;

import com.taken_seat.common_service.message.PaymentRequestMessage;

public interface PaymentRequestListener {

	void handlePaymentRequest(PaymentRequestMessage message);

}
