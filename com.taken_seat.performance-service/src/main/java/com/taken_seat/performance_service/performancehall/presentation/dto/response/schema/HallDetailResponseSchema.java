package com.taken_seat.performance_service.performancehall.presentation.dto.response.schema;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
	name = "HallDetailResponse",
	description = "공연장 상세 조회 응답 DTO"
)
public interface HallDetailResponseSchema
	extends HallCreateResponseSchema {
}