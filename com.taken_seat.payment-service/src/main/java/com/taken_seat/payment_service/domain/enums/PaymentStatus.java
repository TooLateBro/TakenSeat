package com.taken_seat.payment_service.domain.enums;

public enum PaymentStatus {

	COMPLETED("COMPLETED"),
	FAILED("FAILED"),
	REFUNDED("REFUNDED");

	private final String description;

	PaymentStatus(String description) {
		this.description = description;
	}

}
