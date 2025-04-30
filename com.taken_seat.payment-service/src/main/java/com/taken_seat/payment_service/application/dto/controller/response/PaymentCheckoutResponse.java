package com.taken_seat.payment_service.application.dto.controller.response;

import java.util.UUID;

import com.taken_seat.payment_service.domain.model.Payment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class PaymentCheckoutResponse {

	private final UUID bookingId;
	private final String reservationName; // 예: "뮤지컬 <헤드윅>"
	private final int originalAmount;
	private final String customerKey; // 사용자 식별자 (예: UUID.toString())

	public static PaymentCheckoutResponse from(Payment payment, String reservationName) {
		return PaymentCheckoutResponse.of(
			payment.getBookingId(),
			reservationName,
			payment.getPrice(),
			payment.getUserId().toString()
		);
	}

}
