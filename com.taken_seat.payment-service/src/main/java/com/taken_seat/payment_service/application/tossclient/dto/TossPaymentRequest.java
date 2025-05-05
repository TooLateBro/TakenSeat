package com.taken_seat.payment_service.application.tossclient.dto;

public record TossPaymentRequest(
	String paymentKey,
	String orderId,
	Integer amount
) {
}