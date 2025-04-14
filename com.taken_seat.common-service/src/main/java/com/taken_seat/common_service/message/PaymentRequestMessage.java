package com.taken_seat.common_service.message;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequestMessage {

	private UUID bookingId;

	private UUID userId;

	private UUID couponId;

	private Integer mileage;

	private Integer price; // 좌석 금액( 원본 )

}