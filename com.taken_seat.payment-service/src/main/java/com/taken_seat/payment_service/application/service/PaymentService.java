package com.taken_seat.payment_service.application.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.taken_seat.payment_service.application.dto.request.PaymentCreateReqDto;
import com.taken_seat.payment_service.application.dto.response.PaymentCreateResDto;
import com.taken_seat.payment_service.domain.enums.PaymentStatus;
import com.taken_seat.payment_service.domain.model.Payment;
import com.taken_seat.payment_service.domain.model.PaymentHistory;
import com.taken_seat.payment_service.domain.repository.PaymentHistoryRepository;
import com.taken_seat.payment_service.domain.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

	private final PaymentRepository paymentRepository;
	private final PaymentHistoryRepository paymentHistoryRepository;


	public PaymentCreateResDto registerPayment(PaymentCreateReqDto paymentCreateReqDto) {
		// MASTER 계정이 직접 등록하는 API - 결제 API 호출 없이 수동 등록

		if (paymentCreateReqDto.getPrice() <= 0){
			throw new IllegalArgumentException("결제 금액은 1원 미만일 수 없습니다. 요청 금액 : " + paymentCreateReqDto.getPrice());
		}

		LocalDateTime now = LocalDateTime.now();

		Payment payment = Payment.builder()
			.bookingId(paymentCreateReqDto.getBookingId())
			.price(paymentCreateReqDto.getPrice())
			.paymentStatus(PaymentStatus.COMPLETED)
			.approvedAt(now)
			.createdBy(UUID.randomUUID())
			.build();

		paymentRepository.save(payment);

		paymentHistoryRepository.save(
			PaymentHistory.builder()
				.payment(payment)
				.price(payment.getPrice())
				.paymentStatus(payment.getPaymentStatus())
				.approvedAt(now)
				.createdBy(UUID.randomUUID())
				.build()
		);

		return PaymentCreateResDto.toResponse(payment);
	}
}
