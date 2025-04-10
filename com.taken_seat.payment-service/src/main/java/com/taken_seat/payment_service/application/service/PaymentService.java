package com.taken_seat.payment_service.application.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taken_seat.common_service.exception.customException.PaymentNotFoundException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import com.taken_seat.payment_service.application.dto.request.PaymentRegisterReqDto;
import com.taken_seat.payment_service.application.dto.request.PaymentUpdateReqDto;
import com.taken_seat.payment_service.application.dto.response.PagePaymentResponseDto;
import com.taken_seat.payment_service.application.dto.response.PaymentDetailResDto;
import com.taken_seat.payment_service.application.dto.response.PaymentRegisterResDto;
import com.taken_seat.payment_service.application.dto.response.PaymentUpdateResDto;
import com.taken_seat.payment_service.domain.enums.PaymentStatus;
import com.taken_seat.payment_service.domain.model.Payment;
import com.taken_seat.payment_service.domain.model.PaymentHistory;
import com.taken_seat.payment_service.domain.repository.PaymentHistoryRepository;
import com.taken_seat.payment_service.domain.repository.PaymentQuerydslRepository;
import com.taken_seat.payment_service.domain.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PaymentService {

	private final PaymentRepository paymentRepository;
	private final PaymentQuerydslRepository paymentQuerydslRepository;
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
	@CachePut(cacheNames = "paymentCache", key = "#result.paymentId")
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
			.build();

		payment.prePersist(UUID.randomUUID());
		paymentRepository.save(payment);

		PaymentHistory paymentHistory = PaymentHistory.builder()
			.payment(payment)
			.price(payment.getPrice())
			.paymentStatus(payment.getPaymentStatus())
			.approvedAt(now)
			.build();

		paymentHistory.prePersist(UUID.randomUUID());
		paymentHistoryRepository.save(paymentHistory);

		return PaymentRegisterResDto.toResponse(payment);
	}

	@Transactional(readOnly = true)
	@CachePut(cacheNames = "paymentCache", key = "#id")
	public PaymentDetailResDto getPaymentDetail(UUID id) {

		Payment payment = paymentRepository.findByIdAndDeletedAtIsNull(id)
			.orElseThrow(() ->
				new PaymentNotFoundException(ResponseCode.PAYMENT_NOT_FOUND_EXCEPTION,
					"해당 ID 에 대한 결제 정보를 찾을 수 없습니다 : " + id));

		return PaymentDetailResDto.toResponse(payment);
	}

	@Transactional(readOnly = true)
	@Cacheable(cacheNames = "paymentSearchCache", key = "#q + '-' + #category + '-' + #page + '-' + #size")
	public PagePaymentResponseDto searchPayment(String q, String category, int page, int size, String sort,
		String order) {

		Page<Payment> paymentPages = paymentQuerydslRepository.findAll(q, category, page, size, sort, order);

		Page<PaymentDetailResDto> paymentDetailResDtoPages = paymentPages.map(PaymentDetailResDto::toResponse);

		return PagePaymentResponseDto.toResponse(paymentDetailResDtoPages);
	}

	@CachePut(cacheNames = "paymentCache", key = "#id")
	@CacheEvict(cacheNames = "paymentSearchCache", allEntries = true)
	public PaymentUpdateResDto updatePayment(UUID id, PaymentUpdateReqDto paymentUpdateReqDto) {

		if (paymentUpdateReqDto.getPrice() <= 0) {
			throw new IllegalArgumentException("결제 금액은 1원 미만일 수 없습니다. 요청 금액 : " + paymentUpdateReqDto.getPrice());
		}

		Payment payment = paymentRepository.findByIdAndDeletedAtIsNull(id)
			.orElseThrow(() ->
				new PaymentNotFoundException(ResponseCode.PAYMENT_NOT_FOUND_EXCEPTION,
					"해당 ID 에 대한 결제 정보를 찾을 수 없습니다 : " + id));

		payment.update(paymentUpdateReqDto.getPrice(), paymentUpdateReqDto.getPaymentStatus());

		return PaymentUpdateResDto.toResponse(payment);
	}

	@Caching(evict = {
		@CacheEvict(cacheNames = "paymentCache", key = "#id"),
		@CacheEvict(cacheNames = "paymentSearchCache", key = "#id")
	})
	public void deletePayment(UUID id) {
		Payment payment = paymentRepository.findByIdAndDeletedAtIsNull(id)
			.orElseThrow(() ->
				new PaymentNotFoundException(ResponseCode.PAYMENT_NOT_FOUND_EXCEPTION,
					"해당 ID 에 대한 결제 정보를 찾을 수 없습니다 : " + id));

		UUID deletedBy = UUID.randomUUID();
		payment.delete(UUID.randomUUID());
	}
}
