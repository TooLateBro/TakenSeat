package com.taken_seat.payment_service.application.dto.controller.response;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import com.taken_seat.payment_service.domain.enums.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
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

}
