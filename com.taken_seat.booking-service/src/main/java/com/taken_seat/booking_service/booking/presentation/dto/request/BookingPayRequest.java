package com.taken_seat.booking_service.booking.presentation.dto.request;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "결제시 사용할 쿠폰, 마일리지 DTO")
public class BookingPayRequest {
	@Schema(description = "쿠폰 ID")
	private UUID couponId;

	@Schema(description = "마일리지")
	private Integer mileage;
}