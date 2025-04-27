package com.taken_seat.performance_service.performancehall.application.dto.command;

import com.taken_seat.performance_service.performancehall.domain.model.SeatStatus;
import com.taken_seat.performance_service.performancehall.domain.model.SeatType;

public record SeatTemplateInfo(
	String rowNumber,
	String seatNumber,
	SeatType seatType,
	SeatStatus seatStatus
) {
}
