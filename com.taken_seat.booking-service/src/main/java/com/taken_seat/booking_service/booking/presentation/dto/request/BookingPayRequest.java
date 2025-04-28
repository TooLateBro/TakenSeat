package com.taken_seat.booking_service.booking.presentation.dto.request;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BookingPayRequest {
	private UUID couponId;
	private Integer mileage;
}