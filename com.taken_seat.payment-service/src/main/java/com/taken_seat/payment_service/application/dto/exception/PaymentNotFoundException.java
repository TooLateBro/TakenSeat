package com.taken_seat.payment_service.application.dto.exception;

public class PaymentNotFoundException extends RuntimeException {
	public PaymentNotFoundException(String message) {
		super(message);
	}
}
