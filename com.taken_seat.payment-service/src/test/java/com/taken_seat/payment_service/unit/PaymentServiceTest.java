package com.taken_seat.payment_service.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.taken_seat.common_service.dto.AuthenticatedUser;
import com.taken_seat.common_service.exception.customException.PaymentException;
import com.taken_seat.payment_service.application.dto.request.PaymentSearchReqDto;
import com.taken_seat.payment_service.application.dto.response.PagePaymentResponseDto;
import com.taken_seat.payment_service.application.dto.response.PaymentDetailResDto;
import com.taken_seat.payment_service.application.dto.service.PaymentDto;
import com.taken_seat.payment_service.application.service.PaymentServiceImpl;
import com.taken_seat.payment_service.domain.enums.PaymentStatus;
import com.taken_seat.payment_service.domain.model.Payment;
import com.taken_seat.payment_service.domain.model.PaymentHistory;
import com.taken_seat.payment_service.domain.repository.PaymentHistoryRepository;
import com.taken_seat.payment_service.domain.repository.PaymentQuerydslRepository;
import com.taken_seat.payment_service.domain.repository.PaymentRepository;
import com.taken_seat.payment_service.infrastructure.mapper.PaymentMapper;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

	@Mock
	private PaymentRepository paymentRepository;

	@Mock
	private PaymentHistoryRepository paymentHistoryRepository;

	@Mock
	private PaymentQuerydslRepository paymentQuerydslRepository;

	@Mock
	private PaymentMapper paymentMapper;

	@InjectMocks
	private PaymentServiceImpl paymentService;

	private UUID testBookingId;

	private UUID testPaymentId;

	private UUID testPaymentHistoryId;

	private UUID testUserId;

	private Payment testPayment;

	private PaymentHistory testPaymentHistory;

	private PaymentDto testPaymentDto;

	private AuthenticatedUser authenticatedUser;

	@BeforeEach
	void setUp() {
		testUserId = UUID.randomUUID();
		testBookingId = UUID.randomUUID();
		testPaymentId = UUID.randomUUID();
		testPaymentHistoryId = UUID.randomUUID();

		// PaymentDto 초기화
		testPaymentDto = PaymentDto.builder()
			.bookingId(testBookingId)
			.price(1000)
			.userId(UUID.randomUUID())  // 실제 사용자 ID로 설정
			.build();

		testPayment = Payment.builder()
			.id(testPaymentId)
			.bookingId(testBookingId)
			.price(1000)
			.paymentStatus(PaymentStatus.COMPLETED)
			.approvedAt(LocalDateTime.now())
			.build();

		testPayment.prePersist(UUID.randomUUID());

		testPaymentHistory = PaymentHistory.builder()
			.id(testPaymentHistoryId)
			.payment(testPayment)
			.price(testPayment.getPrice())
			.paymentStatus(testPayment.getPaymentStatus())
			.approvedAt(LocalDateTime.now())
			.build();

		testPaymentHistory.prePersist(UUID.randomUUID());

		authenticatedUser = new AuthenticatedUser(testUserId, "test@gmail.com", "MASTER");
	}

	@Test
	@DisplayName("결제 수동 등록 - SUCCESS")
	void testRegisterPayment_success() {
		// Given
		when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment);
		when(paymentHistoryRepository.save(any(PaymentHistory.class))).thenReturn(testPaymentHistory);

		PaymentDetailResDto paymentDetailResDto = PaymentDetailResDto.builder()
			.id(testPaymentDto.getPaymentId())
			.bookingId(testPaymentDto.getBookingId())
			.price(testPaymentDto.getPrice())
			.paymentStatus(testPaymentDto.getPaymentStatus())
			.build();

		when(paymentMapper.toResponse(any(Payment.class))).thenReturn(paymentDetailResDto);

		// When
		PaymentDetailResDto result = paymentService.registerPayment(testPaymentDto);

		// Then
		assertNotNull(result);
		assertEquals(1000, result.getPrice());
		assertEquals(testBookingId, result.getBookingId());
	}

	@Test
	@DisplayName("결제 수동 등록 - 결제 금액이 1원 이하  - FAIL ")
	void testRegisterPayment_fail_zeroOrNegativePrice() {
		// Given
		testPaymentDto = PaymentDto.builder()
			.bookingId(testBookingId)
			.price(0)
			.userId(UUID.randomUUID())  // 실제 사용자 ID로 설정
			.build();

		// When & Then
		PaymentException exception = assertThrows(PaymentException.class, () -> {
			paymentService.registerPayment(testPaymentDto);
		});

		assertEquals("결제 금액은 1원 미만일 수 없습니다. 요청 금액 : 0", exception.getMessage());
	}

	@Test
	@DisplayName("결제 단건 조회 - SUCCESS")
	void testGetPaymentDetail_success() {
		// Given
		when(paymentRepository.findByIdAndDeletedAtIsNull(testPaymentId)).thenReturn(Optional.of(testPayment));

		PaymentDetailResDto paymentDetailResDto = PaymentDetailResDto.builder()
			.id(testPayment.getId())
			.bookingId(testPayment.getBookingId())
			.price(testPayment.getPrice())
			.paymentStatus(testPayment.getPaymentStatus())
			.approvedAt(null)
			.refundAmount(null)
			.refundRequestedAt(null)
			.build();

		when(paymentMapper.toResponse(any(Payment.class))).thenReturn(paymentDetailResDto);

		// When
		PaymentDetailResDto result = paymentService.getPaymentDetail(testPaymentId);

		// Then
		assertNotNull(result);
		assertEquals(testPayment.getPrice(), result.getPrice());
		assertEquals(testPayment.getPaymentStatus(), result.getPaymentStatus());
	}

	@Test
	@DisplayName("결제 단건 조회 실패 - 존재하지않는 UUID로 조회 시도 - FAIL")
	void testGetPaymentDetail_fail_paymentNotFound() {
		// Given
		UUID registerTestPaymentId = UUID.randomUUID();
		when(paymentRepository.findByIdAndDeletedAtIsNull(registerTestPaymentId)).thenReturn(Optional.empty());

		// When & Then
		PaymentException exception = assertThrows(PaymentException.class, () -> {
			paymentService.getPaymentDetail(registerTestPaymentId);
		});

		assertEquals("해당 ID 에 대한 결제 정보를 찾을 수 없습니다 : " + registerTestPaymentId, exception.getMessage());
	}

	@Test
	@DisplayName("결제 리스트 검색 - 상태(status) 필터 적용 - SUCCESS")
	void testSearchPayment_success_withStatusFilter() {
		// Given
		PaymentSearchReqDto searchReqDto = PaymentSearchReqDto.builder()
			.q("REFUNDED")
			.category("status")
			.page(0)
			.size(10)
			.sort("createdAt")
			.order("desc")
			.build();

		Page<Payment> mockPage = new PageImpl<>(Collections.singletonList(testPayment), PageRequest.of(0, 10), 1);

		PaymentDetailResDto paymentDetailResDto = PaymentDetailResDto.builder()
			.id(testPayment.getId())
			.bookingId(testPayment.getBookingId())
			.price(testPayment.getPrice())
			.paymentStatus(testPayment.getPaymentStatus())
			.approvedAt(null)
			.refundAmount(null)
			.refundRequestedAt(null)
			.build();

		when(paymentQuerydslRepository.search(searchReqDto)).thenReturn(mockPage);
		when(paymentMapper.toResponse(testPayment)).thenReturn(paymentDetailResDto);

		// When
		PagePaymentResponseDto result = paymentService.searchPayment(searchReqDto);

		// Then
		assertNotNull(result);
		assertEquals(1, result.getTotalElements());
		assertEquals(1, result.getContent().size());

		PaymentDetailResDto detail = result.getContent().get(0);
		assertEquals(testPayment.getPrice(), detail.getPrice());
		assertEquals(testPayment.getPaymentStatus(), detail.getPaymentStatus());
	}

	@Test
	@DisplayName("결제 리스트 검색 - 비어있는 결과 - SUCCESS")
	void testSearchPayment_success_emptyResult() {
		// Given
		PaymentSearchReqDto searchReqDto = PaymentSearchReqDto.builder()
			.q("REFUNDED")
			.category("status")
			.page(0)
			.size(10)
			.sort("createdAt")
			.order("desc")
			.build();

		when(paymentQuerydslRepository.search(searchReqDto)).thenReturn(Page.empty());

		// When
		PagePaymentResponseDto result = paymentService.searchPayment(searchReqDto);

		// Then
		assertNotNull(result);
		assertEquals(0, result.getTotalElements());
		assertEquals(0, result.getContent().size());
		assertTrue(result.getContent().isEmpty());
	}

	@Test
	@DisplayName("결제 정보 수정 - SUCCESS")
	void testUpdatePayment_success() {
		// Given
		testPaymentDto = PaymentDto.builder()
			.bookingId(testBookingId)
			.paymentId(testPaymentId)
			.price(3000)
			.userId(UUID.randomUUID())  // 실제 사용자 ID로 설정
			.paymentStatus(PaymentStatus.FAILED)
			.build();

		PaymentDetailResDto paymentDetailResDto = PaymentDetailResDto.builder()
			.id(testPayment.getId())
			.bookingId(testPaymentDto.getBookingId())
			.price(testPaymentDto.getPrice())
			.paymentStatus(testPaymentDto.getPaymentStatus())
			.approvedAt(null)
			.refundAmount(null)
			.refundRequestedAt(null)
			.build();

		when(paymentRepository.findByIdAndDeletedAtIsNull(testPaymentDto.getPaymentId())).thenReturn(
			Optional.of(testPayment));
		when(paymentHistoryRepository.findByPayment(testPayment)).thenReturn(Optional.of(testPaymentHistory));

		when(paymentMapper.toResponse(any(Payment.class))).thenReturn(paymentDetailResDto);

		// When
		PaymentDetailResDto result = paymentService.updatePayment(testPaymentDto);

		// Then
		assertNotNull(result);
		assertEquals(3000, result.getPrice());
		assertEquals(PaymentStatus.FAILED, result.getPaymentStatus());

	}

	@Test
	@DisplayName("결제 정보 수정 실패 - 결제 금액이 0원 이하 - FAIL")
	void testUpdatePayment_fail_zeroOrNegativePrice() {
		// Given
		testPaymentDto = PaymentDto.builder()
			.bookingId(testBookingId)
			.paymentId(testPaymentId)
			.price(0)
			.userId(UUID.randomUUID())  // 실제 사용자 ID로 설정
			.paymentStatus(PaymentStatus.FAILED)
			.build();

		// When & Then
		PaymentException exception = assertThrows(PaymentException.class, () -> {
			paymentService.updatePayment(testPaymentDto);
		});

		assertEquals("결제 금액은 1원 미만일 수 없습니다. 요청 금액 : 0", exception.getMessage());
	}

	@Test
	@DisplayName("결제 정보 수정 실패 - 존재하지 않는 결제 ID - FAIL")
	void testUpdatePayment_fail_paymentNotFound() {
		// Given
		UUID notExistId = UUID.randomUUID();

		testPaymentDto = PaymentDto.builder()
			.bookingId(testBookingId)
			.paymentId(notExistId)
			.price(1000)
			.userId(UUID.randomUUID())  // 실제 사용자 ID로 설정
			.paymentStatus(PaymentStatus.FAILED)
			.build();

		when(paymentRepository.findByIdAndDeletedAtIsNull(testPaymentDto.getPaymentId())).thenReturn(Optional.empty());

		// When & Then
		PaymentException exception = assertThrows(PaymentException.class, () -> {
			paymentService.updatePayment(testPaymentDto);
		});

		assertEquals("해당 ID 에 대한 결제 정보를 찾을 수 없습니다 : " + notExistId, exception.getMessage());
	}

	@Test
	@DisplayName("결제 정보 수정 - PaymentHistory 없음 - FAIL")
	void testUpdatePayment_fail_paymentHistoryNotFound() {
		// Given
		testPaymentDto = PaymentDto.builder()
			.bookingId(testBookingId)
			.paymentId(testPaymentId)
			.price(1000)
			.userId(UUID.randomUUID())  // 실제 사용자 ID로 설정
			.paymentStatus(PaymentStatus.FAILED)
			.build();

		when(paymentRepository.findByIdAndDeletedAtIsNull(testPaymentId)).thenReturn(Optional.of(testPayment));
		when(paymentHistoryRepository.findByPayment(testPayment)).thenReturn(Optional.empty());

		// When & Then
		RuntimeException exception = assertThrows(RuntimeException.class, () ->
			paymentService.updatePayment(testPaymentDto)
		);

		assertTrue(exception.getMessage().contains("해당 결제의 내역이 존재하지않습니다."));
	}

	@Test
	@DisplayName("결제 논리적 삭제 성공 - SUCCESS")
	void testDeletePayment_success() {
		// Given
		UUID deletedBy = UUID.randomUUID();

		when(paymentRepository.findByIdAndDeletedAtIsNull(testPaymentId)).thenReturn(Optional.of(testPayment));
		when(paymentHistoryRepository.findByPayment(testPayment)).thenReturn(Optional.of(testPaymentHistory));

		// When
		assertDoesNotThrow(() -> paymentService.deletePayment(testPaymentId, authenticatedUser));

		// Then
		assertNotNull(testPayment.getDeletedAt());
		assertNotNull(testPaymentHistory.getDeletedAt());
	}

	@Test
	@DisplayName("결제 논리적 삭제 실패 - 존재하지않는 결제 ID - FAIL")
	void testDeletePayment_fail_paymentNotFound() {
		// Given
		when(paymentRepository.findByIdAndDeletedAtIsNull(any(UUID.class))).thenReturn(Optional.empty());

		// When & Then
		PaymentException exception = assertThrows(PaymentException.class, () ->
			paymentService.deletePayment(UUID.randomUUID(), authenticatedUser)
		);

		assertTrue(exception.getMessage().contains("해당 ID 에 대한 결제 정보를 찾을 수 없습니다"));

	}
}
