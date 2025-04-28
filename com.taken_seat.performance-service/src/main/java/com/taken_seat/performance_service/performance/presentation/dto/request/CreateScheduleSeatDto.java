package com.taken_seat.performance_service.performance.presentation.dto.request;

import com.taken_seat.performance_service.performance.presentation.dto.request.schema.CreateScheduleSeatSchema;
import com.taken_seat.performance_service.performancehall.domain.model.SeatStatus;
import com.taken_seat.performance_service.performancehall.domain.model.SeatType;

public record CreateScheduleSeatDto(
	String rowNumber,
	String seatNumber,
	SeatType seatType,
	SeatStatus seatStatus,
	Integer price
) implements CreateScheduleSeatSchema {
}

