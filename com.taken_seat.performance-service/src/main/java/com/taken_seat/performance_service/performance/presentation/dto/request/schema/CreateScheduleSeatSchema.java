package com.taken_seat.performance_service.performance.presentation.dto.request.schema;

import com.taken_seat.performance_service.performancehall.domain.model.SeatStatus;
import com.taken_seat.performance_service.performancehall.domain.model.SeatType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "CreateScheduleSeat", description = "스케줄 좌석 생성 DTO")
public interface CreateScheduleSeatSchema {
	@Schema(
		description = "좌석 행 번호",
		example = "A")
	String rowNumber();

	@Schema(
		description = "좌석 번호",
		example = "10")
	String seatNumber();

	@Schema(
		description = "좌석 타입",
		example = "VIP, S, R, A")
	SeatType seatType();

	@Schema(
		description = "좌석 상태",
		example = "AVAILABLE, SOLDOUT, DISABLED ")
	SeatStatus seatStatus();

	@Schema(
		description = "좌석 가격",
		example = "50000")
	Integer price();
}
