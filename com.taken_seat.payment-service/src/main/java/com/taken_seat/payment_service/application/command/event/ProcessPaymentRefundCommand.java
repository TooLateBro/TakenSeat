package com.taken_seat.payment_service.application.command.event;

import com.taken_seat.common_service.message.PaymentRefundMessage;
import com.taken_seat.payment_service.application.command.PaymentCommand;
import com.taken_seat.payment_service.application.service.event.PaymentRefundEventHandlerService;

public class ProcessPaymentRefundCommand implements PaymentCommand<Void> {

	private final PaymentRefundEventHandlerService paymentRefundEventHandlerService;
	private final PaymentRefundMessage paymentRefundMessage;

	public ProcessPaymentRefundCommand(PaymentRefundEventHandlerService paymentRefundEventHandlerService,
		PaymentRefundMessage paymentRefundMessage) {
		this.paymentRefundEventHandlerService = paymentRefundEventHandlerService;
		this.paymentRefundMessage = paymentRefundMessage;
	}

	@Override
	public Void execute() {
		paymentRefundEventHandlerService.processPaymentRefund(paymentRefundMessage);
		return null;
	}
}
