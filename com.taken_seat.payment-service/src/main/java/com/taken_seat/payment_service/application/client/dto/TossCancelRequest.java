package com.taken_seat.payment_service.application.client.dto;

public record TossCancelRequest(
	Integer cancelAmount,
	String cancelReason
) {
}
