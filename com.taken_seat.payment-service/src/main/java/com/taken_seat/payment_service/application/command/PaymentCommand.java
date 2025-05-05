package com.taken_seat.payment_service.application.command;

public interface PaymentCommand<R> {
	R execute();
}
