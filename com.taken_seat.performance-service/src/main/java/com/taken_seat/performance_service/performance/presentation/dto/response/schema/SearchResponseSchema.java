package com.taken_seat.performance_service.performance.presentation.dto.response.schema;

import java.time.LocalDateTime;
import java.util.UUID;

import com.taken_seat.performance_service.performance.domain.model.PerformanceStatus;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "SearchResponse", description = "공연 검색 결과 응답 DTO")
public interface SearchResponseSchema {

	@Schema(
		description = "공연 ID (UUID)",
		example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
	UUID performanceId();

	@Schema(
		description = "공연 제목",
		example = "뮤지컬 라이온킹")
	String title();

	@Schema(
		description = "공연 시작 일시 (yyyy-MM-dd HH:mm:ss)",
		example = "2025-06-01 19:00:00")
	LocalDateTime startAt();

	@Schema(
		description = "공연 종료 일시 (yyyy-MM-dd HH:mm:ss)",
		example = "2025-06-01 21:30:00")
	LocalDateTime endAt();

	@Schema(
		description = "공연 상태",
		example = "UPCOMING")
	PerformanceStatus status();

	@Schema(
		description = "포스터 이미지 URL",
		example = "https://example.com/poster.png")
	String posterUrl();
}
