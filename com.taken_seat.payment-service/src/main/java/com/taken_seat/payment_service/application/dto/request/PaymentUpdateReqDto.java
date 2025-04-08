package com.taken_seat.payment_service.application.dto.request;

import com.taken_seat.payment_service.domain.enums.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentUpdateReqDto {

	private Integer price;

	private PaymentStatus paymentStatus;
}
