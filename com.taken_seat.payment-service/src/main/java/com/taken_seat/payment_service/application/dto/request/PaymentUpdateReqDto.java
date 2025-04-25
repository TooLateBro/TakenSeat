package com.taken_seat.payment_service.application.dto.request;

import com.taken_seat.payment_service.domain.enums.PaymentStatus;

import jakarta.validation.constraints.NotNull;

public record PaymentUpdateReqDto(
	@NotNull(message = "결제 금액은 필수입니다.")
	Integer price,

	@NotNull(message = "결제 상태 는 필수입니다.")
	PaymentStatus paymentStatus
) {
}