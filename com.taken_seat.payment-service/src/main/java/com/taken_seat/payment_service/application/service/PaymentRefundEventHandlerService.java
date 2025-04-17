package com.taken_seat.payment_service.application.service;

import com.taken_seat.common_service.message.PaymentRefundMessage;

public interface PaymentRefundEventHandlerService {

	void processPaymentRefund(PaymentRefundMessage message);
}
