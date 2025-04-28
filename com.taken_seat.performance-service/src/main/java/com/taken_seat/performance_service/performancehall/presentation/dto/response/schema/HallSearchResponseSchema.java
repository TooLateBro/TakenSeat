package com.taken_seat.performance_service.performancehall.presentation.dto.response.schema;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
	name = "HallSearchResponse",
	description = "공연장 검색 응답 DTO"
)
public interface HallSearchResponseSchema {

	@Schema(
		description = "공연장 ID (UUID)",
		example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
	UUID performanceHallId();

	@Schema(
		description = "공연장 이름",
		example = "서울 올림픽홀")
	String name();

	@Schema(
		description = "공연장 총 좌석 수",
		example = "5000")
	Integer totalSeats();
}
