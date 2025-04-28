package com.taken_seat.performance_service.performance.presentation.dto.response;

import java.util.UUID;

import com.taken_seat.performance_service.performance.presentation.dto.response.schema.ScheduleSeatResponseSchema;
import com.taken_seat.performance_service.performancehall.domain.model.SeatStatus;
import com.taken_seat.performance_service.performancehall.domain.model.SeatType;

public record ScheduleSeatResponseDto(
	UUID scheduleSeatId,
	String rowNumber,
	String seatNumber,
	SeatType seatType,
	SeatStatus seatStatus,
	Integer price
) implements ScheduleSeatResponseSchema {
}

