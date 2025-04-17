package com.taken_seat.payment_service.application.kafka.consumer;

import com.taken_seat.common_service.message.PaymentRefundMessage;

public interface PaymentRefundRequestListener {

	void handlerRefundRequest(PaymentRefundMessage message);
}
