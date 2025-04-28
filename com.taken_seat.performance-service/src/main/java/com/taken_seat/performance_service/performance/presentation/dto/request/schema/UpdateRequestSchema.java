package com.taken_seat.performance_service.performance.presentation.dto.request.schema;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.taken_seat.performance_service.performance.domain.model.PerformanceStatus;
import com.taken_seat.performance_service.performance.presentation.dto.request.UpdatePerformanceScheduleDto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "UpdateRequest", description = "공연 수정 요청 DTO")
public interface UpdateRequestSchema {
	@Schema(
		description = "공연 ID (UUID)",
		example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
	UUID performanceId();

	@Schema(
		description = "공연 제목",
		example = "라이온킹")
	String title();

	@Schema(
		description = "공연 설명",
		example = "전 세계가 사랑하는 라이온킹 내한공연")
	String description();

	@Schema(
		description = "공연 시작 일시 (yyyy-MM-dd HH:mm:ss)",
		example = "2025-06-01 19:00:00")
	LocalDateTime startAt();

	@Schema(
		description = "공연 종료 일시 (yyyy-MM-dd HH:mm:ss)",
		example = "2025-06-01 21:30:00")
	LocalDateTime endAt();

	@Schema(
		description = "공연 상태 (CREATED, PUBLISHED 등)",
		example = "PUBLISHED")
	PerformanceStatus status();

	@Schema(
		description = "포스터 이미지 URL",
		example = "https://example.com/poster.png")
	String posterUrl();

	@Schema(
		description = "관람 제한 연령",
		example = "15")
	String ageLimit();

	@Schema(
		description = "최대 티켓 구매 가능 수량",
		example = "4")
	Integer maxTicketCount();

	@Schema(
		description = "할인 정보",
		example = "조기 예매 10% 할인")
	String discountInfo();

	@Schema(description = "공연 회차 수정 정보 리스트")
	List<UpdatePerformanceScheduleDto> schedules();
}
