package com.taken_seat.performance_service.performancehall.presentation.dto.request.schema;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
	name = "HallSearchFilterParam",
	description = "공연장 검색 필터 파라미터"
)
public interface HallSearchFilterParamSchema {
	@Schema(
		description = "공연장 이름 (부분 일치)",
		example = "올림픽홀"
	)
	String name();

	@Schema(
		description = "공연장 주소 (부분 일치)",
		example = "송파구"
	)
	String address();

	@Schema(
		description = "최대 좌석 수",
		example = "500"
	)
	Integer maxSeats();

	@Schema(
		description = "최소 좌석 수",
		example = "100"
	)
	Integer minSeats();
}
