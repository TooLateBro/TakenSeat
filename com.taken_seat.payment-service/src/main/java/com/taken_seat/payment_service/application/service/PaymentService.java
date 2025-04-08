package com.taken_seat.payment_service.application.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.taken_seat.payment_service.application.dto.request.PaymentRegisterReqDto;
import com.taken_seat.payment_service.application.dto.response.PaymentRegisterResDto;
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

	/**
	 * 결제 수동 등록 기능
	 *
	 * 주어진 결제 수동 결제 요청 DTO ( PaymentCreateReqDto )를 기반으로 새 결제를 등록한다.
	 * 1. 결제 요청 금액이 1원 미만인지 검사한다.
	 *   - 1원 미만인 경우 IllegalArgumentException 예외처리 발생
	 * 2. Payment 등록 이후 결제 이력 추적용 PaymentHistory 생성
	 * 3. 저장된 결제를 DTO ( PaymentCreateDto ) 형식으로 변환하여 반환한다.
	 *
	 * @param paymentRegisterReqDto 등록할 결제의 정보
	 * @return PaymentRegisterResDto 등록된 결제의 정보
	 * @throws IllegalArgumentException 결제 요청 금액이 1원 미만인 경우 예외 발생
	 */
	public PaymentRegisterResDto registerPayment(PaymentRegisterReqDto paymentRegisterReqDto) {
		// MASTER 계정이 직접 등록하는 API - 결제 API 호출 없이 수동 등록

		if (paymentRegisterReqDto.getPrice() <= 0) {
			throw new IllegalArgumentException("결제 금액은 1원 미만일 수 없습니다. 요청 금액 : " + paymentRegisterReqDto.getPrice());
		}

		LocalDateTime now = LocalDateTime.now();

		Payment payment = Payment.builder()
			.bookingId(paymentRegisterReqDto.getBookingId())
			.price(paymentRegisterReqDto.getPrice())
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

		return PaymentRegisterResDto.toResponse(payment);
	}
}
