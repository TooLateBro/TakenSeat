package com.taken_seat.performance_service.performancehall.application.dto.command;

import java.util.UUID;

import com.taken_seat.performance_service.performancehall.domain.model.SeatStatus;
import com.taken_seat.performance_service.performancehall.domain.model.SeatType;
import com.taken_seat.performance_service.performancehall.presentation.dto.request.BaseSeatDto;

public record UpdateSeatCommand(
	UUID seatId,
	String rowNumber,
	String seatNumber,
	SeatType seatType,
	SeatStatus status
) implements BaseSeatDto {

	@Override
	public String getRowNumber() {
		return rowNumber;
	}

	@Override
	public String getSeatNumber() {
		return seatNumber;
	}
}
