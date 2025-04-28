package com.taken_seat.performance_service.performancehall.presentation.dto.response.schema;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
	name = "HallUpdateResponse",
	description = "공연장 수정 응답 DTO"
)
public interface HallUpdateResponseSchema
	extends HallCreateResponseSchema {
}
