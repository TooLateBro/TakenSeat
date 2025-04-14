package com.taken_seat.payment_service.application.service;

import com.taken_seat.common_service.message.PaymentRequestMessage;

public interface PaymentEventHandlerService {

	void processPayment(PaymentRequestMessage message);
}
