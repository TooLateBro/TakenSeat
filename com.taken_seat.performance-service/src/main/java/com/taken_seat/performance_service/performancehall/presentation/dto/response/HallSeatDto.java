package com.taken_seat.performance_service.performancehall.presentation.dto.response;

import java.util.UUID;

import com.taken_seat.performance_service.performancehall.domain.model.SeatStatus;
import com.taken_seat.performance_service.performancehall.domain.model.SeatType;
import com.taken_seat.performance_service.performancehall.presentation.dto.response.schema.HallSeatSchema;

public record HallSeatDto(
	UUID seatId,
	String rowNumber,
	String seatNumber,
	SeatType seatType,
	SeatStatus status
) implements HallSeatSchema {
}
