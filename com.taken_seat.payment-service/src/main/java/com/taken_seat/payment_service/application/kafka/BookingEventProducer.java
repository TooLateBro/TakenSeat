package com.taken_seat.payment_service.application.kafka;

import com.taken_seat.common_service.message.PaymentResultMessage;

public interface BookingEventProducer {

	void sendPaymentResult(PaymentResultMessage message);

}
