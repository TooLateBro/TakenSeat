package com.taken_seat.common_service.message;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequestMessage {

	private UUID bookingId;

	private UUID userId;

	private Integer price;
}