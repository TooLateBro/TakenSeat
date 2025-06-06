package com.taken_seat.payment_service.application.command.api;

import java.util.UUID;

import com.taken_seat.payment_service.application.command.PaymentCommand;
import com.taken_seat.payment_service.application.dto.controller.response.PaymentDetailResDto;
import com.taken_seat.payment_service.application.service.api.PaymentService;

public class GetPaymentDetailCommand implements PaymentCommand<PaymentDetailResDto> {

	private final PaymentService paymentService;

	private final UUID paymentId;

	public GetPaymentDetailCommand(PaymentService paymentService, UUID paymentId) {
		this.paymentService = paymentService;
		this.paymentId = paymentId;
	}

	@Override
	public PaymentDetailResDto execute() {
		return paymentService.getPaymentDetail(paymentId);
	}
}
