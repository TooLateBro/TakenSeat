package com.taken_seat.common_service.dto.request.schema;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
	name = "BookingSeatClientRequest",
	description = "좌석 예약 요청 DTO"
)
public interface BookingSeatClientRequestSchema {

	@Schema(
		description = "공연 ID (UUID)",
		example = "123e4567-e89b-12d3-a456-426614174000"
	)
	UUID performanceId();

	@Schema(
		description = "공연 회차 ID (UUID)",
		example = "223e4567-e89b-12d3-a456-426614174001"
	)
	UUID performanceScheduleId();

	@Schema(
		description = "스케줄 좌석 ID (UUID)",
		example = "323e4567-e89b-12d3-a456-426614174002"
	)
	UUID scheduleSeatId();
}
