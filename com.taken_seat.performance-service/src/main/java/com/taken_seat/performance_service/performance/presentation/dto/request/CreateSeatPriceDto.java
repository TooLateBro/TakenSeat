package com.taken_seat.performance_service.performance.presentation.dto.request;

import com.taken_seat.performance_service.performancehall.domain.model.SeatType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateSeatPriceDto(
	@NotNull(message = "좌석 타입은 필수입니다.")
	SeatType seatType,

	@NotNull(message = "좌석 가격은 필수입니다.")
	@Positive(message = "좌석 가격은 0보다 커야 합니다.")
	Integer price
) {
}
