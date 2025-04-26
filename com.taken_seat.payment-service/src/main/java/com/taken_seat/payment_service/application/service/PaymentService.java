package com.taken_seat.payment_service.application.service;

import java.util.UUID;

import com.taken_seat.common_service.dto.AuthenticatedUser;
import com.taken_seat.payment_service.application.dto.request.PaymentSearchReqDto;
import com.taken_seat.payment_service.application.dto.response.PagePaymentResponseDto;
import com.taken_seat.payment_service.application.dto.response.PaymentDetailResDto;
import com.taken_seat.payment_service.application.dto.service.PaymentDto;

public interface PaymentService {

	PaymentDetailResDto registerPayment(PaymentDto dto);

	PaymentDetailResDto getPaymentDetail(UUID id);

	PagePaymentResponseDto searchPayment(PaymentSearchReqDto searchReqDto);

	PaymentDetailResDto updatePayment(PaymentDto dto);

	void deletePayment(UUID id, AuthenticatedUser authenticatedUser);
}
