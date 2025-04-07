package com.taken_seat.payment_service.application.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCreateReqDto {

	@NotNull(message = "예매 ID는 필수입니다.")
	private UUID bookingId;

	@NotNull(message = "결제 금액은 필수입니다.")
	private Integer price;
}
