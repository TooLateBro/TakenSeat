package com.taken_seat.performance_service.performance.presentation.dto.response;

import java.util.UUID;

import com.taken_seat.performance_service.performancehall.domain.model.SeatStatus;
import com.taken_seat.performance_service.performancehall.domain.model.SeatType;

public record ScheduleSeatResponseDto(
	UUID id,
	String rowNumber,
	String seatNumber,
	SeatType seatType,
	SeatStatus seatStatus,
	Integer price
) {
}

