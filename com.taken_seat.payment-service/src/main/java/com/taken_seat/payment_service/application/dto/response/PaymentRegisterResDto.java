package com.taken_seat.payment_service.application.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.taken_seat.payment_service.domain.enums.PaymentStatus;
import com.taken_seat.payment_service.domain.model.Payment;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRegisterResDto {

	private UUID paymentId;

	private UUID bookingId;

	private Integer price;

	private PaymentStatus paymentStatus;

	private LocalDateTime approvedAt;

	public static PaymentRegisterResDto toResponse(Payment payment) {
		return PaymentRegisterResDto.builder()
			.paymentId(payment.getId())
			.bookingId(payment.getBookingId())
			.price(payment.getPrice())
			.paymentStatus(payment.getPaymentStatus())
			.approvedAt(payment.getApprovedAt())
			.build();
	}
}
