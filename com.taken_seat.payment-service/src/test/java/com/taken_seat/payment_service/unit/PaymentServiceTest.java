package com.taken_seat.payment_service.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.taken_seat.payment_service.application.dto.request.PaymentRegisterReqDto;
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

	private Payment testPayment;

	private PaymentHistory testPaymentHistory;

	@BeforeEach
	void setUp() {
		testBookingId = UUID.randomUUID();

		testPayment = Payment.builder()
			.bookingId(testBookingId)
			.price(1000)
			.paymentStatus(PaymentStatus.COMPLETED)
			.approvedAt(LocalDateTime.now())
			.createdBy(UUID.randomUUID())
			.build();

		testPaymentHistory = PaymentHistory.builder()
			.payment(testPayment)
			.price(testPayment.getPrice())
			.paymentStatus(testPayment.getPaymentStatus())
			.approvedAt(LocalDateTime.now())
			.createdBy(UUID.randomUUID())
			.build();
	}

	@Test
	@DisplayName("결제 수동 등록 - SUCCESS")
	void registerPayment_success() {
		// Given
		PaymentRegisterReqDto paymentRegisterReqDto = new PaymentRegisterReqDto(testBookingId, 1000);

		when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment);
		when(paymentHistoryRepository.save(any(PaymentHistory.class))).thenReturn(testPaymentHistory);

		// When
		PaymentRegisterResDto result = paymentService.registerPayment(paymentRegisterReqDto);

		// Then
		assertNotNull(result);
		assertEquals(testBookingId, result.getBookingId());
		assertEquals(1000, result.getPrice());
	}

	@Test
	@DisplayName("결제 수동 등록 - 결제 금액이 1원 이하  - Fail ")
	void registerPayment_fail_zeroOrNegativePrice() {
		// Given
		PaymentRegisterReqDto paymentRegisterReqDto = new PaymentRegisterReqDto(testBookingId, 0);

		// When & Then
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			paymentService.registerPayment(paymentRegisterReqDto);
		});

		assertEquals("결제 금액은 1원 미만일 수 없습니다. 요청 금액 : 0", exception.getMessage());
	}

}
