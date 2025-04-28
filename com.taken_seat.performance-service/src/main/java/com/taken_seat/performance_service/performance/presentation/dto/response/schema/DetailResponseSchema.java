package com.taken_seat.performance_service.performance.presentation.dto.response.schema;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "DetailResponse", description = "공연 상세 조회 응답 DTO")
public interface DetailResponseSchema extends CreateResponseSchema {
}
