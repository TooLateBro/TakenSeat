package com.taken_seat.performance_service.performance.presentation.dto.request.schema;

import java.time.LocalDateTime;
import java.util.List;

import com.taken_seat.performance_service.performance.domain.model.PerformanceStatus;
import com.taken_seat.performance_service.performance.presentation.dto.request.CreatePerformanceScheduleDto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "CreateRequest", description = "공연 생성 요청 DTO")
public interface CreateRequestSchema {
	@Schema(description = "공연 제목", example = "뮤지컬 라이온킹")
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
		description = "공연 상태 (UPCOMING, LIVE, SOLDOUT, CLOSED)",
		example = "UPCOMING")
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
		example = "2")
	Integer maxTicketCount();

	@Schema(
		description = "할인 정보",
		example = "조기 예매 10% 할인")
	String discountInfo();

	@Schema(description = "공연 회차 정보 리스트")
	List<CreatePerformanceScheduleDto> schedules();
}
