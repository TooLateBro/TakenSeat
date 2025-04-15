package com.taken_seat.payment_service.application.service;

import com.taken_seat.common_service.message.PaymentMessage;

public interface PaymentEventHandlerService {

	void processPayment(PaymentMessage message);
}
