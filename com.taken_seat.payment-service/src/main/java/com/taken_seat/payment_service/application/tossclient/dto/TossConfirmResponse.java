package com.taken_seat.payment_service.application.tossclient.dto;

public record TossConfirmResponse(
	String orderId,
	String paymentKey,
	String status,
	String approvedAt,
	String method,
	int totalAmount,
	CardInfo card
) {
	public record CardInfo(
		String number,
		String company
	) {
	}
}