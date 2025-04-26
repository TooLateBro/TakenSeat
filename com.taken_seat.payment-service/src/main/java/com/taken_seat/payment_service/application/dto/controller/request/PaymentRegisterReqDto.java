package com.taken_seat.payment_service.application.dto.controller.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record PaymentRegisterReqDto(

	@NotNull(message = "예매 ID는 필수입니다.")
	UUID bookingId,

	@NotNull(message = "결제 금액은 필수입니다.")
	Integer price

) {
}