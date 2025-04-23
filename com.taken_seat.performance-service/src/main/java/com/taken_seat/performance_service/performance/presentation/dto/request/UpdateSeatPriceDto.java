package com.taken_seat.performance_service.performance.presentation.dto.request;

import java.util.UUID;

import com.taken_seat.performance_service.performancehall.domain.model.SeatType;

import jakarta.validation.constraints.Positive;

public record UpdateSeatPriceDto(
	UUID performanceSeatPriceId,
	SeatType seatType,

	@Positive(message = "좌석 가격은 0보다 커야 합니다.")
	Integer price
) {
}
