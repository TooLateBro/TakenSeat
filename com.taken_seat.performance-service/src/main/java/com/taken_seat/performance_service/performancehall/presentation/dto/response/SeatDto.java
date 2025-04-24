package com.taken_seat.performance_service.performancehall.presentation.dto.response;

import java.util.UUID;

import com.taken_seat.performance_service.performancehall.domain.model.SeatStatus;
import com.taken_seat.performance_service.performancehall.domain.model.SeatType;

public record SeatDto(
	UUID seatId,
	String rowNumber,
	String seatNumber,
	SeatType seatType,
	SeatStatus status
) {
}
