package com.taken_seat.common_service.dto.response.schema;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
	name = "TicketPerformanceClientResponse",
	description = "티켓 정보 조회 응답 DTO"
)
public interface TicketPerformanceClientResponseSchema {

	@Schema(
		description = "공연 제목",
		example = "뮤지컬 라이온킹"
	)
	String title();

	@Schema(
		description = "공연장 이름",
		example = "서울 올림픽홀"
	)
	String name();

	@Schema(
		description = "공연장 주소",
		example = "서울특별시 송파구 올림픽로 424"
	)
	String address();

	@Schema(
		description = "좌석 행 번호",
		example = "A"
	)
	String rowNumber();

	@Schema(
		description = "좌석 번호",
		example = "10"
	)
	String seatNumber();

	@Schema(
		description = "좌석 타입",
		example = "VIP"
	)
	String seatType();

	@Schema(
		description = "공연 시작 일시 (yyyy-MM-dd HH:mm)",
		example = "2025-06-01 19:00"
	)
	LocalDateTime startAt();

	@Schema(
		description = "공연 종료 일시 (yyyy-MM-dd HH:mm)",
		example = "2025-06-01 21:30"
	)
	LocalDateTime endAt();
}
