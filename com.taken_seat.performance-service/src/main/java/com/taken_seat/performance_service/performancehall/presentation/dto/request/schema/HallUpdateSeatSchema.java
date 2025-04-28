package com.taken_seat.performance_service.performancehall.presentation.dto.request.schema;

import java.util.UUID;

import com.taken_seat.performance_service.performancehall.domain.model.SeatStatus;
import com.taken_seat.performance_service.performancehall.domain.model.SeatType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
	name = "HallUpdateSeat",
	description = "공연장 좌석 수정 DTO"
)
public interface HallUpdateSeatSchema extends BaseSeatSchema {
	@Schema(
		description = "좌석 고유 ID (UUID)",
		example = "4fa85f64-5717-4562-b3fc-2c963f66afa7"
	)
	UUID seatId();

	@Schema(
		description = "좌석 등급",
		example = "S"
	)
	SeatType seatType();

	@Schema(
		description = "좌석 상태",
		example = "SOLDOUT"
	)
	SeatStatus status();
}
