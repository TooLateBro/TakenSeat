package com.taken_seat.performance_service.performance.presentation.dto.request.schema;

import java.time.LocalDateTime;

import com.taken_seat.performance_service.performance.domain.model.PerformanceStatus;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "SearchFilterParam", description = "공연 검색 필터 파라미터")
public interface SearchFilterParamSchema {
	@Schema(
		description = "공연 제목(부분 일치)",
		example = "라이온킹")
	String title();

	@Schema(
		description = "검색 시작 일시 (yyyy-MM-dd HH:mm:ss)",
		example = "2025-05-01 00:00:00")
	LocalDateTime startAt();

	@Schema(
		description = "검색 종료 일시 (yyyy-MM-dd HH:mm:ss)",
		example = "2025-05-31 23:59:59")
	LocalDateTime endAt();

	@Schema(
		description = "공연 상태 (UPCOMING, LIVE, SOLDOUT, CLOSED)",
		example = "UPCOMING")
	PerformanceStatus status();
}
