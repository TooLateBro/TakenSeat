package com.taken_seat.performance_service.performance.presentation.dto.request;

import java.util.UUID;

import com.taken_seat.performance_service.performance.presentation.dto.request.schema.UpdateScheduleSeatSchema;
import com.taken_seat.performance_service.performancehall.domain.model.SeatStatus;
import com.taken_seat.performance_service.performancehall.domain.model.SeatType;

public record UpdateScheduleSeatDto(
	UUID scheduleSeatId,
	String rowNumber,
	String seatNumber,
	SeatType seatType,
	SeatStatus seatStatus,
	Integer price
) implements UpdateScheduleSeatSchema {
}
