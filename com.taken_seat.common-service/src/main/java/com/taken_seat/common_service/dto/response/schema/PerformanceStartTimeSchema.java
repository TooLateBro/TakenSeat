package com.taken_seat.common_service.dto.response.schema;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
	name = "PerformanceStartTime",
	description = "공연 시작 시간 조회 응답 DTO"
)
public interface PerformanceStartTimeSchema {

	@Schema(
		description = "시작 일시 (yyyy-MM-dd HH:mm)",
		example = "2025-06-01 19:00"
	)
	LocalDateTime startAt();
}