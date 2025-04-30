package com.taken_seat.payment_service.application.client.dto;

public record TossPaymentRequest(
	String paymentKey,
	String orderId,
	Integer amount
) {
}