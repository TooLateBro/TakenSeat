package com.taken_seat.performance_service.performance.presentation.dto.response;

import java.util.UUID;

import com.taken_seat.performance_service.performancehall.domain.model.SeatType;

public record SeatPriceResponseDto(
	UUID performanceSeatPriceId,
	SeatType seatType,
	Integer price
) {
}
