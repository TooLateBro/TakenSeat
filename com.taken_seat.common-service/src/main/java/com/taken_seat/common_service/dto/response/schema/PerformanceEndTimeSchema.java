package com.taken_seat.common_service.dto.response.schema;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
	name = "PerformanceEndTime",
	description = "공연 종료 시간 조회 응답 DTO"
)
public interface PerformanceEndTimeSchema {

	@Schema(
		description = "종료 일시 (yyyy-MM-dd HH:mm)",
		example = "2025-06-01 21:30"
	)
	LocalDateTime endAt();
}

