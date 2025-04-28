package com.taken_seat.performance_service.performancehall.presentation.dto.request.schema;

import com.taken_seat.performance_service.performancehall.domain.model.SeatStatus;
import com.taken_seat.performance_service.performancehall.domain.model.SeatType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
	name = "HallCreateSeat",
	description = "공연장 좌석 생성 DTO"
)
public interface HallCreateSeatSchema extends BaseSeatSchema {
	@Schema(
		description = "좌석 등급",
		example = "VIP"
	)
	SeatType seatType();

	@Schema(
		description = "좌석 상태",
		example = "AVAILABLE"
	)
	SeatStatus status();
}
