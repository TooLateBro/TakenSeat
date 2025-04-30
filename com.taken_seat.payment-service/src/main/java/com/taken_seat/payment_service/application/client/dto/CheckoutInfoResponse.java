package com.taken_seat.payment_service.application.client.dto;

public record CheckoutInfoResponse(
	String bookingId,
	String orderName,
	int originalAmount,
	String customerKey
) {
}