package com.taken_seat.payment_service.application.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.taken_seat.payment_service.domain.enums.PaymentStatus;
import com.taken_seat.payment_service.domain.model.Payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDetailResDto {

	private UUID paymentId;

	private UUID bookingId;

	private Integer price;

	private PaymentStatus paymentStatus;

	private LocalDateTime approvedAt;

	private Integer refundAmount;

	private LocalDateTime refundRequestedAt;

	public static PaymentDetailResDto toResponse(Payment payment) {
		return PaymentDetailResDto.builder()
			.paymentId(payment.getId())
			.price(payment.getPrice())
			.bookingId(payment.getBookingId())
			.paymentStatus(payment.getPaymentStatus())
			.approvedAt(payment.getApprovedAt())
			.refundAmount(payment.getRefundAmount())
			.refundRequestedAt(payment.getRefundRequestedAt())
			.build();
	}

}
