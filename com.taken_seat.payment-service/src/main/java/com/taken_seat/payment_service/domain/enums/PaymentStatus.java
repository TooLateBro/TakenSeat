package com.taken_seat.payment_service.domain.enums;

public enum PaymentStatus {
	
	PENDING("PENDING"),
	COMPLETED("COMPLETED"),
	FAILED("FAILED"),
	REFUNDED("REFUNDED"),
	DELETED("DELETED");

	private final String description;

	PaymentStatus(String description) {
		this.description = description;
	}

}
