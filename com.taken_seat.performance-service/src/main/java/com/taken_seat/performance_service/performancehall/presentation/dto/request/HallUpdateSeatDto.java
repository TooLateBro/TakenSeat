package com.taken_seat.performance_service.performancehall.presentation.dto.request;

import java.util.UUID;

import com.taken_seat.performance_service.performancehall.domain.model.SeatStatus;
import com.taken_seat.performance_service.performancehall.domain.model.SeatType;
import com.taken_seat.performance_service.performancehall.presentation.dto.request.schema.HallUpdateSeatSchema;

import jakarta.validation.constraints.Pattern;

public record HallUpdateSeatDto(
	UUID seatId,

	@Pattern(regexp = "^[A-Z]{1,2}$", message = "좌석 열(rowNumber)은 A~Z 또는 AA~ZZ 형식이어야 합니다.")
	String rowNumber,

	@Pattern(regexp = "^(?!0{1,5}$)\\d{1,5}$", message = "좌석 번호는 1~99999 사이의 숫자 형식이어야 하며, 0으로만 이루어질 수 없습니다.")
	String seatNumber,

	SeatType seatType,
	SeatStatus status
) implements BaseSeatDto, HallUpdateSeatSchema {

	@Override
	public String getRowNumber() {
		return rowNumber;
	}

	@Override
	public String getSeatNumber() {
		return seatNumber;
	}
}
