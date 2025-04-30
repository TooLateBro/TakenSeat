package com.taken_seat.payment_service.application.client;

import com.taken_seat.payment_service.application.client.dto.TossConfirmResponse;
import com.taken_seat.payment_service.application.client.dto.TossPaymentRequest;

public interface TossPaymentClient {
	TossConfirmResponse confirmPayment(TossPaymentRequest request);
}
