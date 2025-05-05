package com.taken_seat.payment_service.application.command.api;

import com.taken_seat.payment_service.application.command.PaymentCommand;
import com.taken_seat.payment_service.application.dto.controller.response.PagePaymentResponseDto;
import com.taken_seat.payment_service.application.dto.service.PaymentSearchDto;
import com.taken_seat.payment_service.application.service.api.PaymentService;

public class SearchPaymentCommand implements PaymentCommand<PagePaymentResponseDto> {

	private final PaymentService paymentService;
	private final PaymentSearchDto dto;

	public SearchPaymentCommand(PaymentService paymentService, PaymentSearchDto dto) {
		this.paymentService = paymentService;
		this.dto = dto;
	}

	@Override
	public PagePaymentResponseDto execute() {
		return paymentService.searchPayment(dto);
	}
}
