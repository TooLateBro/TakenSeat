package com.taken_seat.payment_service.application.command.event;

import org.springframework.stereotype.Component;

import com.taken_seat.common_service.message.PaymentMessage;
import com.taken_seat.payment_service.application.command.PaymentCommand;
import com.taken_seat.payment_service.application.service.event.PaymentEventHandlerService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ProcessPaymentCommand implements PaymentCommand<Void> {

	private final PaymentEventHandlerService paymentEventHandlerService;
	private final PaymentMessage paymentMessage;

	public ProcessPaymentCommand(PaymentEventHandlerService paymentEventHandlerService, PaymentMessage paymentMessage) {
		this.paymentEventHandlerService = paymentEventHandlerService;
		this.paymentMessage = paymentMessage;
	}

	@Override
	public Void execute() {
		paymentEventHandlerService.processPayment(paymentMessage);
		return null;
	}
}
