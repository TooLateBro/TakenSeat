package com.taken_seat.performance_service.performancehall.presentation.dto.request.schema;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
	name = "BaseSeat",
	description = "공통 좌석 정보 DTO"
)
public interface BaseSeatSchema {
	@Schema(
		description = "좌석 행 번호 (A, B, ... AA, AB 등)",
		example = "A"
	)
	String rowNumber();

	@Schema(
		description = "좌석 번호",
		example = "10"
	)
	String seatNumber();
}
