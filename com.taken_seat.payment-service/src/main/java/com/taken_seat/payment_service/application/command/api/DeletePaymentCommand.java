package com.taken_seat.payment_service.application.command.api;

import java.util.UUID;

import com.taken_seat.common_service.dto.AuthenticatedUser;
import com.taken_seat.payment_service.application.command.PaymentCommand;
import com.taken_seat.payment_service.application.service.api.PaymentService;

public class DeletePaymentCommand implements PaymentCommand<Void> {

	private final PaymentService paymentService;
	private final AuthenticatedUser authenticatedUser;
	private final UUID paymentId;

	public DeletePaymentCommand(PaymentService paymentService, UUID paymentId, AuthenticatedUser authenticatedUser) {
		this.paymentService = paymentService;
		this.authenticatedUser = authenticatedUser;
		this.paymentId = paymentId;
	}

	@Override
	public Void execute() {
		paymentService.deletePayment(paymentId, authenticatedUser);
		return null;
	}
}
