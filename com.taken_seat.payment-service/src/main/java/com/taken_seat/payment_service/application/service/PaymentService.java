package com.taken_seat.payment_service.application.service;

import java.util.UUID;

import com.taken_seat.common_service.dto.AuthenticatedUser;
import com.taken_seat.payment_service.application.dto.request.PaymentRegisterReqDto;
import com.taken_seat.payment_service.application.dto.request.PaymentUpdateReqDto;
import com.taken_seat.payment_service.application.dto.response.PagePaymentResponseDto;
import com.taken_seat.payment_service.application.dto.response.PaymentDetailResDto;
import com.taken_seat.payment_service.application.dto.response.PaymentRegisterResDto;
import com.taken_seat.payment_service.application.dto.response.PaymentUpdateResDto;

public interface PaymentService {

	PaymentRegisterResDto registerPayment(PaymentRegisterReqDto paymentRegisterReqDto,
		AuthenticatedUser authenticatedUser);

	PaymentDetailResDto getPaymentDetail(UUID id);

	PagePaymentResponseDto searchPayment(String q, String category, int page, int size, String sort,
		String order);

	PaymentUpdateResDto updatePayment(UUID id, PaymentUpdateReqDto paymentUpdateReqDto,
		AuthenticatedUser authenticatedUser);

	void deletePayment(UUID id, AuthenticatedUser authenticatedUser);
}
