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

import com.taken_seat.common_service.exception.customException.PaymentNotFoundException;
import com.taken_seat.payment_service.application.dto.request.PaymentRegisterReqDto;
import com.taken_seat.payment_service.application.dto.request.PaymentUpdateReqDto;
import com.taken_seat.payment_service.application.dto.response.PagePaymentResponseDto;
import com.taken_seat.payment_service.application.dto.response.PaymentDetailResDto;
import com.taken_seat.payment_service.application.dto.response.PaymentRegisterResDto;
import com.taken_seat.payment_service.application.dto.response.PaymentUpdateResDto;
import com.taken_seat.payment_service.application.service.PaymentService;
import com.taken_seat.payment_service.domain.enums.PaymentStatus;
import com.taken_seat.payment_service.domain.model.Payment;
import com.taken_seat.payment_service.domain.model.PaymentHistory;
import com.taken_seat.payment_service.domain.repository.PaymentHistoryRepository;
import com.taken_seat.payment_service.domain.repository.PaymentQuerydslRepository;
import com.taken_seat.payment_service.domain.repository.PaymentRepository;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

	@Mock
	private PaymentRepository paymentRepository;

	@Mock
	private PaymentHistoryRepository paymentHistoryRepository;

	@Mock
	private PaymentQuerydslRepository paymentQuerydslRepository;

	@InjectMocks
	private PaymentService paymentService;

	private UUID testBookingId;

	private UUID testPaymentId;

	private UUID testPaymentHistoryId;

	private Payment testPayment;

	private PaymentHistory testPaymentHistory;

	@BeforeEach
	void setUp() {
		testBookingId = UUID.randomUUID();

		testPaymentId = UUID.randomUUID();
		testPaymentHistoryId = UUID.randomUUID();

		testPayment = Payment.builder()
			.id(testPaymentId)
			.bookingId(testBookingId)
			.price(1000)
			.paymentStatus(PaymentStatus.COMPLETED)
			.approvedAt(LocalDateTime.now())
			.build();

		testPayment.prePersist(UUID.randomUUID());
		paymentRepository.save(testPayment);

		testPaymentHistory = PaymentHistory.builder()
			.id(testPaymentHistoryId)
			.payment(testPayment)
			.price(testPayment.getPrice())
			.paymentStatus(testPayment.getPaymentStatus())
			.approvedAt(LocalDateTime.now())
			.build();

		testPaymentHistory.prePersist(UUID.randomUUID());
		paymentHistoryRepository.save(testPaymentHistory);
	}

	@Test
	@DisplayName("결제 수동 등록 - SUCCESS")
	void testRegisterPayment_success() {
		// Given
		UUID registerTestPaymentId = UUID.randomUUID();
		UUID createdBy = UUID.randomUUID();
		PaymentRegisterReqDto paymentRegisterReqDto = new PaymentRegisterReqDto(registerTestPaymentId, 1000);

		when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment);
		when(paymentHistoryRepository.save(any(PaymentHistory.class))).thenReturn(testPaymentHistory);

		// When
		PaymentRegisterResDto result = paymentService.registerPayment(paymentRegisterReqDto, createdBy);

		// Then
		assertNotNull(result);
		assertEquals(registerTestPaymentId, result.getBookingId());
		assertEquals(1000, result.getPrice());
	}

	@Test
	@DisplayName("결제 수동 등록 - 결제 금액이 1원 이하  - FAIL ")
	void testRegisterPayment_fail_zeroOrNegativePrice() {
		// Given
		UUID registerTestPaymentId = UUID.randomUUID();
		UUID createdBy = UUID.randomUUID();

		PaymentRegisterReqDto paymentRegisterReqDto = new PaymentRegisterReqDto(registerTestPaymentId, 0);

		// When & Then
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			paymentService.registerPayment(paymentRegisterReqDto, createdBy);
		});

		assertEquals("결제 금액은 1원 미만일 수 없습니다. 요청 금액 : 0", exception.getMessage());
	}

	@Test
	@DisplayName("결제 단건 조회 - SUCCESS")
	void testGetPaymentDetail_success() {
		// Given
		when(paymentRepository.findByIdAndDeletedAtIsNull(testPaymentId)).thenReturn(Optional.of(testPayment));

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
		PaymentNotFoundException exception = assertThrows(PaymentNotFoundException.class, () -> {
			paymentService.getPaymentDetail(registerTestPaymentId);
		});

		assertEquals("해당 ID 에 대한 결제 정보를 찾을 수 없습니다 : " + registerTestPaymentId, exception.getMessage());
	}

	@Test
	@DisplayName("결제 리스트 검색 - 상태(status) 필터 적용 - SUCCESS")
	void testSearchPayment_success_withStatusFilter() {
		// Given
		String query = "COMPLETED";
		String category = "status";
		int page = 0;
		int size = 10;
		String sort = "createdAt";
		String order = "desc";

		Page<Payment> mockPage = new PageImpl<>(Collections.singletonList(testPayment), PageRequest.of(page, size), 1);

		when(paymentQuerydslRepository.findAll(query, category, page, size, sort, order)).thenReturn(mockPage);

		// When
		PagePaymentResponseDto result = paymentService.searchPayment(query, category, page, size, sort, order);

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
		when(paymentQuerydslRepository.findAll(anyString(), anyString(), anyInt(), anyInt(), anyString(), anyString()))
			.thenReturn(Page.empty());

		// When
		PagePaymentResponseDto result = paymentService.searchPayment("REFUNDED", "status", 0, 10, "createdAt", "desc");

		// Then
		assertNotNull(result);
		assertEquals(0, result.getTotalElements());
		assertEquals(0, result.getContent().size());

		assertEquals(0, result.getTotalElements());
		assertTrue(result.getContent().isEmpty());
	}

	@Test
	@DisplayName("결제 정보 수정 - SUCCESS")
	void testUpdatePayment_success() {
		// Given
		PaymentUpdateReqDto paymentUpdateReqDto = new PaymentUpdateReqDto(3000, PaymentStatus.FAILED);
		UUID updatedBy = UUID.randomUUID();

		when(paymentRepository.findByIdAndDeletedAtIsNull(testPaymentId)).thenReturn(Optional.of(testPayment));
		when(paymentHistoryRepository.findByPayment(testPayment)).thenReturn(Optional.of(testPaymentHistory));

		// When
		PaymentUpdateResDto result = paymentService.updatePayment(testPaymentId, paymentUpdateReqDto, updatedBy);

		// Then
		assertNotNull(result);
		assertEquals(3000, result.getPrice());
		assertEquals(PaymentStatus.FAILED, result.getPaymentStatus());

	}

	@Test
	@DisplayName("결제 정보 수정 실패 - 결제 금액이 0원 이하 - FAIL")
	void testUpdatePayment_fail_zeroOrNegativePrice() {
		// Given
		PaymentUpdateReqDto reqDto = new PaymentUpdateReqDto(0, PaymentStatus.FAILED);
		UUID updatedBy = UUID.randomUUID();

		// When & Then
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			paymentService.updatePayment(testPaymentId, reqDto, updatedBy);
		});

		assertEquals("결제 금액은 1원 미만일 수 없습니다. 요청 금액 : 0", exception.getMessage());
	}

	@Test
	@DisplayName("결제 정보 수정 실패 - 존재하지 않는 결제 ID - FAIL")
	void testUpdatePayment_fail_paymentNotFound() {
		// Given
		UUID notExistId = UUID.randomUUID();
		UUID updatedBy = UUID.randomUUID();
		PaymentUpdateReqDto reqDto = new PaymentUpdateReqDto(3000, PaymentStatus.FAILED);

		when(paymentRepository.findByIdAndDeletedAtIsNull(notExistId)).thenReturn(Optional.empty());

		// When & Then
		PaymentNotFoundException exception = assertThrows(PaymentNotFoundException.class, () -> {
			paymentService.updatePayment(notExistId, reqDto, updatedBy);
		});

		assertEquals("해당 ID 에 대한 결제 정보를 찾을 수 없습니다 : " + notExistId, exception.getMessage());
	}

	@Test
	@DisplayName("결제 정보 수정 - PaymentHistory 없음 - FAIL")
	void testUpdatePayment_fail_paymentHistoryNotFound() {
		// Given
		UUID updatedBy = UUID.randomUUID();
		PaymentUpdateReqDto updateDto = new PaymentUpdateReqDto(2000, PaymentStatus.COMPLETED);

		when(paymentRepository.findByIdAndDeletedAtIsNull(testPaymentId)).thenReturn(Optional.of(testPayment));
		when(paymentHistoryRepository.findByPayment(testPayment)).thenReturn(Optional.empty());

		// When & Then
		RuntimeException exception = assertThrows(RuntimeException.class, () ->
			paymentService.updatePayment(testPaymentId, updateDto, updatedBy)
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
		assertDoesNotThrow(() -> paymentService.deletePayment(testPaymentId, deletedBy));

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
		PaymentNotFoundException exception = assertThrows(PaymentNotFoundException.class, () ->
			paymentService.deletePayment(UUID.randomUUID(), UUID.randomUUID())
		);

		assertTrue(exception.getMessage().contains("해당 ID 에 대한 결제 정보를 찾을 수 없습니다"));

	}
}
