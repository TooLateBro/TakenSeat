package com.taken_seat.performance_service.performance.application.dto.command;

import java.util.UUID;

import com.taken_seat.performance_service.performancehall.domain.model.SeatStatus;
import com.taken_seat.performance_service.performancehall.domain.model.SeatType;

public record UpdateScheduleSeatCommand(
	UUID scheduleSeatId,
	String rowNumber,
	String seatNumber,
	SeatType seatType,
	SeatStatus seatStatus,
	Integer price
) {
}

