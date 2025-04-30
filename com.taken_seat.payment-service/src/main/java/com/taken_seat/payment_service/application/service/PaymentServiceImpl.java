package com.taken_seat.payment_service.application.service;

import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taken_seat.common_service.dto.AuthenticatedUser;
import com.taken_seat.common_service.exception.customException.PaymentException;
import com.taken_seat.common_service.exception.customException.PaymentHistoryException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import com.taken_seat.payment_service.application.client.TossPaymentClient;
import com.taken_seat.payment_service.application.client.dto.TossConfirmResponse;
import com.taken_seat.payment_service.application.client.dto.TossPaymentRequest;
import com.taken_seat.payment_service.application.dto.controller.response.PagePaymentResponseDto;
import com.taken_seat.payment_service.application.dto.controller.response.PaymentCheckoutResponse;
import com.taken_seat.payment_service.application.dto.controller.response.PaymentDetailResDto;
import com.taken_seat.payment_service.application.dto.service.PaymentDto;
import com.taken_seat.payment_service.application.dto.service.PaymentSearchDto;
import com.taken_seat.payment_service.domain.model.Payment;
import com.taken_seat.payment_service.domain.model.PaymentHistory;
import com.taken_seat.payment_service.domain.repository.PaymentHistoryRepository;
import com.taken_seat.payment_service.domain.repository.PaymentQuerydslRepository;
import com.taken_seat.payment_service.domain.repository.PaymentRepository;
import com.taken_seat.payment_service.infrastructure.mapper.PaymentMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

	private final PaymentRepository paymentRepository;
	private final PaymentQuerydslRepository paymentQuerydslRepository;
	private final PaymentHistoryRepository paymentHistoryRepository;
	private final TossPaymentClient tossPaymentClient;
	private final PaymentMapper paymentMapper;

	/**
	 * 결제 수동 등록 기능
	 * <p>
	 * 주어진 결제 수동 결제 요청 DTO ( PaymentCreateReqDto )를 기반으로 새 결제를 등록한다.
	 * 1. 결제 요청 금액이 1원 미만인지 검사한다.
	 * - 1원 미만인 경우 IllegalArgumentException 예외처리 발생
	 * 2. Payment 등록 이후 결제 이력 추적용 PaymentHistory 생성
	 * 3. 저장된 결제를 DTO ( PaymentCreateDto ) 형식으로 변환하여 반환한다.
	 *
	 * @param dto 등록할 결제의 정보
	 * @return PaymentRegisterResDto 등록된 결제의 정보
	 * @throws IllegalArgumentException 결제 요청 금액이 1원 미만인 경우 예외 발생
	 */
	@Override
	@CachePut(cacheNames = "paymentCache", key = "#result.id")
	public PaymentDetailResDto registerPayment(PaymentDto dto) {
		// MASTER 계정이 직접 등록하는 API - 결제 API 호출 없이 수동 등록

		if (dto.getPrice() <= 0) {
			throw new PaymentException(ResponseCode.ILLEGAL_ARGUMENT,
				"결제 금액은 1원 미만일 수 없습니다. 요청 금액 : " + dto.getPrice());
		}

		Payment payment = Payment.register(dto);
		paymentRepository.save(payment);

		PaymentHistory paymentHistory = PaymentHistory.register(payment);
		paymentHistoryRepository.save(paymentHistory);

		return paymentMapper.toResponse(payment);
	}

	/**
	 * 결제 상세 조회 기능
	 *
	 * 주어진 결제 ID를 기반으로 상세 결제 정보를 조회한다.
	 * 1. 삭제되지 않은 Payment를 조회하며, 존재하지않을 경우 PaymentNotFoundException을 발생시킨다.
	 * 2. 조회된 결제를 PaymentDetailResDto로 변환하여 반환한다.
	 *
	 * @param id 조회할 결제 ID
	 * @return 조회된 결제 상세 응답 DTO
	 * @throws PaymentException 결제 ID에 해당하는 정보가 존재하지 않는 경우 예외 발생
	 */
	@Override
	@Transactional(readOnly = true)
	@CachePut(cacheNames = "paymentCache", key = "#id")
	public PaymentDetailResDto getPaymentDetail(UUID id) {

		Payment payment = paymentRepository.findByIdAndDeletedAtIsNull(id)
			.orElseThrow(() ->
				new PaymentException(ResponseCode.PAYMENT_NOT_FOUND_EXCEPTION,
					"해당 ID 에 대한 결제 정보를 찾을 수 없습니다 : " + id));

		return paymentMapper.toResponse(payment);
	}

	/**
	 * 결제 검색 기능
	 *
	 * 주어진 조건을 기반으로 결제 목록을 검색한다. ( 캐싱 적용 )
	 * 검색 조건에 따라 결제를 조회하고, 결과를 페이징 처리하여 반환한다.
	 *
	 * 이 과정에서 `@Cacheable`을 사용하여 검색 조건에 맞는 주문 목록을 캐시한다. 캐시가 존재하면 빠르게 반환하며,
	 * 캐시가 없을 경우 DB에서 검색하여 결과를 반환한다.
	 *
	 * @param searchReqDto 검색 조건을 담고 있는 DTO 객체
	 * searchReqDto.q 검색어 (optional)
	 * searchReqDto.category 검색 카테고리 (결제 상태, 결제 승인 일자, 환불 요청 일자)
	 * searchReqDto.page 페이지 번호 (0부터 시작)
	 * searchReqDto.size 페이지 크기
	 * searchReqDto.sort 정렬 기준 (예: createdAt, updatedAt)
	 * searchReqDto.order 정렬 순서 (ASC 또는 DESC)
	 * @return 페이징 처리된 검색 결과를 포함한 PagePaymentResponseDto 객체
	 */
	@Override
	@Transactional(readOnly = true)
	@Cacheable(cacheNames = "paymentSearchCache", key = "#searchReqDto.q + '-' + #searchReqDto.category + '-' + #searchReqDto.page + '-' + #searchReqDto.size")
	public PagePaymentResponseDto searchPayment(PaymentSearchDto searchReqDto) {

		Page<Payment> paymentPages = paymentQuerydslRepository.search(searchReqDto);

		Page<PaymentDetailResDto> paymentDetailResDtoPages = paymentPages.map(paymentMapper::toResponse);

		return PagePaymentResponseDto.toResponse(paymentDetailResDtoPages);
	}

	/**
	 * 결제 수정 기능
	 * <p>
	 * 주어진 결제 ID와 수정 요청 DTO를 기반으로 결제 정보를 수정한다.
	 * 1. 결제 금액이 1원 미만일 경우 IllegalArgumentException 예외를 발생시킨다.
	 * 2. 삭제되지 않은 Payment와 관련된 PaymentHistory를 조회한다.
	 * - 둘 중 하나라도 없을 경우 각각 PaymentNotFoundException 또는 PaymentHistoryNotFoundException 발생
	 * 3. 결제 정보(Payment)를 수정하고, 이력(PaymentHistory)도 함께 수정한다.
	 * 4. 수정된 결제 정보를 PaymentUpdateResDto로 변환하여 반환한다.
	 *
	 * @param dto 수정할 결제 정보를 담고 있는 DTO
	 * @return PaymentDetailResDto 수정된 결제 응답 DTO
	 * @throws IllegalArgumentException 결제 금액이 1원 미만인 경우 예외 발생
	 * @throws PaymentException         결제가 존재하지 않는 경우 예외 발생
	 * @throws PaymentHistoryException  결제 이력이 존재하지 않는 경우 예외 발생
	 */
	@Override
	@CachePut(cacheNames = "paymentCache", key = "#dto.paymentId")
	@CacheEvict(cacheNames = "paymentSearchCache", allEntries = true)
	public PaymentDetailResDto updatePayment(PaymentDto dto) {

		if (dto.getPrice() <= 0) {
			throw new PaymentException(ResponseCode.ILLEGAL_ARGUMENT,
				"결제 금액은 1원 미만일 수 없습니다. 요청 금액 : " + dto.getPrice());
		}

		Payment payment = paymentRepository.findByIdAndDeletedAtIsNull(dto.getPaymentId())
			.orElseThrow(() ->
				new PaymentException(ResponseCode.PAYMENT_NOT_FOUND_EXCEPTION,
					"해당 ID 에 대한 결제 정보를 찾을 수 없습니다 : " + dto.getPaymentId()));

		PaymentHistory paymentHistory = paymentHistoryRepository.findByPayment(payment)
			.orElseThrow(() ->
				new PaymentHistoryException(ResponseCode.PAYMENT_HISTORY_NOT_FOUND_EXCEPTION));

		payment.update(dto);
		paymentHistory.updateHistory(payment);
		return paymentMapper.toResponse(payment);
	}

	/**
	 * 결제 삭제 기능 (Soft Delete)
	 *
	 * 주어진 결제 ID를 기반으로 결제 및 결제 이력을 논리적으로 삭제 처리한다.
	 * 1. 삭제되지 않은 Payment와 관련된 PaymentHistory를 조회한다.
	 *    - 존재하지 않을 경우 각각 PaymentNotFoundException 또는 PaymentHistoryNotFoundException 발생
	 * 2. Payment 및 PaymentHistory의 삭제 처리 메서드를 호출한다.
	 * 3. 캐시에서 관련 결제 및 검색 캐시 제거
	 *
	 * @param id 삭제할 결제 ID
	 * @param authenticatedUser 인증된 사용자 정보 (삭제자 ID 포함)
	 * @throws PaymentException 결제가 존재하지 않는 경우 예외 발생
	 * @throws PaymentHistoryException 결제 이력이 존재하지 않는 경우 예외 발생
	 */
	@Override
	@Caching(evict = {
		@CacheEvict(cacheNames = "paymentCache", key = "#id"),
		@CacheEvict(cacheNames = "paymentSearchCache", key = "#id")
	})
	public void deletePayment(UUID id, AuthenticatedUser authenticatedUser) {
		Payment payment = paymentRepository.findByIdAndDeletedAtIsNull(id)
			.orElseThrow(() ->
				new PaymentException(ResponseCode.PAYMENT_NOT_FOUND_EXCEPTION,
					"해당 ID 에 대한 결제 정보를 찾을 수 없습니다 : " + id));

		PaymentHistory paymentHistory = paymentHistoryRepository.findByPayment(payment)
			.orElseThrow(() ->
				new PaymentHistoryException(ResponseCode.PAYMENT_HISTORY_NOT_FOUND_EXCEPTION));

		payment.delete(authenticatedUser.getUserId());
		paymentHistory.delete(authenticatedUser.getUserId());
	}

	@Override
	public PaymentCheckoutResponse getCheckoutInfo(UUID bookingId) {

		Payment payment = paymentRepository.findByBookingIdAndDeletedAtIsNull(bookingId)
			.orElseThrow(() ->
				new PaymentException(ResponseCode.PAYMENT_NOT_FOUND_EXCEPTION,
					"해당 예매 ID 에 대한 결제 정보를 찾을 수 없습니다 : " + bookingId));

		return PaymentCheckoutResponse.from(payment);
	}

	@Override
	public TossConfirmResponse confirmPayment(TossPaymentRequest request) {
		return tossPaymentClient.confirmPayment(request);
	}
}
