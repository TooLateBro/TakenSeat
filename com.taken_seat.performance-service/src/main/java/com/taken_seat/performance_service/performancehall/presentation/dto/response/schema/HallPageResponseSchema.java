package com.taken_seat.performance_service.performancehall.presentation.dto.response.schema;

import java.util.List;

import com.taken_seat.performance_service.performancehall.presentation.dto.response.HallSearchResponseDto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
	name = "HallPageResponse",
	description = "공연장 목록 페이징 응답 DTO"
)
public interface HallPageResponseSchema {

	@Schema(
		description = "검색된 공연장 목록")
	List<HallSearchResponseDto> content();

	@Schema(
		description = "페이지 당 항목 수",
		example = "10")
	int pageSize();

	@Schema(
		description = "현재 페이지 번호 (0부터 시작)",
		example = "0")
	int pageNumber();

	@Schema(
		description = "전체 페이지 수",
		example = "5")
	int totalPages();

	@Schema(
		description = "전체 요소 수",
		example = "45")
	long totalElements();

	@Schema(
		description = "마지막 페이지 여부",
		example = "false")
	boolean isLast();
}
