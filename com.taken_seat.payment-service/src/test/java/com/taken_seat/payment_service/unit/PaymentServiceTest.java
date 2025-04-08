package com.taken_seat.payment_service.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.taken_seat.payment_service.application.dto.exception.PaymentNotFoundException;
import com.taken_seat.payment_service.application.dto.request.PaymentRegisterReqDto;
import com.taken_seat.payment_service.application.dto.response.PaymentDetailResDto;
import com.taken_seat.payment_service.application.dto.response.PaymentRegisterResDto;
import com.taken_seat.payment_service.application.service.PaymentService;
import com.taken_seat.payment_service.domain.enums.PaymentStatus;
import com.taken_seat.payment_service.domain.model.Payment;
import com.taken_seat.payment_service.domain.model.PaymentHistory;
import com.taken_seat.payment_service.domain.repository.PaymentHistoryRepository;
import com.taken_seat.payment_service.domain.repository.PaymentRepository;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

	@Mock
	private PaymentRepository paymentRepository;

	@Mock
	private PaymentHistoryRepository paymentHistoryRepository;

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
			.createdBy(UUID.randomUUID())
			.build();

		paymentRepository.save(testPayment);

		testPaymentHistory = PaymentHistory.builder()
			.id(testPaymentHistoryId)
			.payment(testPayment)
			.price(testPayment.getPrice())
			.paymentStatus(testPayment.getPaymentStatus())
			.approvedAt(LocalDateTime.now())
			.createdBy(UUID.randomUUID())
			.build();

		paymentHistoryRepository.save(testPaymentHistory);
	}

	@Test
	@DisplayName("결제 수동 등록 - SUCCESS")
	void registerPayment_success() {
		// Given
		UUID registerTestPaymentId = UUID.randomUUID();

		PaymentRegisterReqDto paymentRegisterReqDto = new PaymentRegisterReqDto(registerTestPaymentId, 1000);

		when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment);
		when(paymentHistoryRepository.save(any(PaymentHistory.class))).thenReturn(testPaymentHistory);

		// When
		PaymentRegisterResDto result = paymentService.registerPayment(paymentRegisterReqDto);

		// Then
		assertNotNull(result);
		assertEquals(registerTestPaymentId, result.getBookingId());
		assertEquals(1000, result.getPrice());
	}

	@Test
	@DisplayName("결제 수동 등록 - 결제 금액이 1원 이하  - FAIL ")
	void registerPayment_fail_zeroOrNegativePrice() {
		// Given
		UUID registerTestPaymentId = UUID.randomUUID();

		PaymentRegisterReqDto paymentRegisterReqDto = new PaymentRegisterReqDto(registerTestPaymentId, 0);

		// When & Then
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			paymentService.registerPayment(paymentRegisterReqDto);
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
}
