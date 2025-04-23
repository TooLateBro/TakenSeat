package com.taken_seat.performance_service.performance.application.dto.command;

import com.taken_seat.performance_service.performancehall.domain.model.SeatType;

public record CreateSeatPriceCommand(
	SeatType seatType,
	Integer price
) {
}
