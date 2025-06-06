package com.taken_seat.payment_service.application.service.api;

import java.util.UUID;

import com.taken_seat.common_service.dto.AuthenticatedUser;
import com.taken_seat.payment_service.application.dto.controller.response.PagePaymentResponseDto;
import com.taken_seat.payment_service.application.dto.controller.response.PaymentCheckoutResponse;
import com.taken_seat.payment_service.application.dto.controller.response.PaymentDetailResDto;
import com.taken_seat.payment_service.application.dto.service.PaymentDto;
import com.taken_seat.payment_service.application.dto.service.PaymentSearchDto;
import com.taken_seat.payment_service.application.tossclient.dto.TossConfirmResponse;
import com.taken_seat.payment_service.application.tossclient.dto.TossPaymentRequest;

public interface PaymentService {

	PaymentDetailResDto registerPayment(PaymentDto dto);

	PaymentDetailResDto getPaymentDetail(UUID id);

	PagePaymentResponseDto searchPayment(PaymentSearchDto searchReqDto);

	PaymentDetailResDto updatePayment(PaymentDto dto);

	void deletePayment(UUID id, AuthenticatedUser authenticatedUser);

	PaymentCheckoutResponse getCheckoutInfo(UUID bookingId);

	TossConfirmResponse confirmPayment(TossPaymentRequest request);
}
