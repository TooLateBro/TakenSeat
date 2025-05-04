package com.taken_seat.payment_service.application.tossclient.dto;

public record TossCancelRequest(
	Integer cancelAmount,
	String cancelReason
) {
}
