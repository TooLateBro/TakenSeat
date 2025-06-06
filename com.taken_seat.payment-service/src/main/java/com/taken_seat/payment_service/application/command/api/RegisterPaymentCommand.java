package com.taken_seat.payment_service.application.command.api;

import com.taken_seat.payment_service.application.command.PaymentCommand;
import com.taken_seat.payment_service.application.dto.controller.response.PaymentDetailResDto;
import com.taken_seat.payment_service.application.dto.service.PaymentDto;
import com.taken_seat.payment_service.application.service.api.PaymentService;

public class RegisterPaymentCommand implements PaymentCommand<PaymentDetailResDto> {

	private final PaymentService paymentService;
	private final PaymentDto dto;

	public RegisterPaymentCommand(PaymentService paymentService, PaymentDto dto) {
		this.paymentService = paymentService;
		this.dto = dto;
	}

	@Override
	public PaymentDetailResDto execute() {
		return paymentService.registerPayment(dto);
	}
}
