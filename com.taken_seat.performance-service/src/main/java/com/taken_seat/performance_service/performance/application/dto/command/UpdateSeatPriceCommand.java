package com.taken_seat.performance_service.performance.application.dto.command;

import java.util.UUID;

import com.taken_seat.performance_service.performancehall.domain.model.SeatType;

public record UpdateSeatPriceCommand(
	UUID performanceSeatPriceId,
	SeatType seatType,
	Integer price
) {
}
