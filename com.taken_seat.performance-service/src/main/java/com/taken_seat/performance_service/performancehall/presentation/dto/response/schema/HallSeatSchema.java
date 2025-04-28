package com.taken_seat.performance_service.performancehall.presentation.dto.response.schema;

import java.util.UUID;

import com.taken_seat.performance_service.performancehall.domain.model.SeatStatus;
import com.taken_seat.performance_service.performancehall.domain.model.SeatType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
	name = "HallSeat",
	description = "공연장 좌석 응답 DTO"
)
public interface HallSeatSchema {

	@Schema(
		description = "좌석 ID (UUID)",
		example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
	UUID seatId();

	@Schema(
		description = "좌석 행 번호",
		example = "A")
	String rowNumber();

	@Schema(
		description = "좌석 번호",
		example = "10")
	String seatNumber();

	@Schema(
		description = "좌석 등급",
		example = "VIP")
	SeatType seatType();

	@Schema(
		description = "좌석 상태",
		example = "AVAILABLE")
	SeatStatus status();
}
