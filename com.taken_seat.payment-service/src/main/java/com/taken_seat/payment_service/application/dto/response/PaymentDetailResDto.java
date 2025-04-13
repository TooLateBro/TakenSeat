package com.taken_seat.payment_service.application.dto.response;

import java.io.Serializable;
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
public class PaymentDetailResDto implements Serializable {

	private UUID id;

	private UUID bookingId;

	private Integer price;

	private PaymentStatus paymentStatus;

	private LocalDateTime approvedAt;

	private Integer refundAmount;

	private LocalDateTime refundRequestedAt;

	public static PaymentDetailResDto toResponse(Payment payment) {
		return PaymentDetailResDto.builder()
			.id(payment.getId())
			.price(payment.getPrice())
			.bookingId(payment.getBookingId())
			.paymentStatus(payment.getPaymentStatus())
			.approvedAt(payment.getApprovedAt())
			.refundAmount(payment.getRefundAmount())
			.refundRequestedAt(payment.getRefundRequestedAt())
			.build();
	}

}
