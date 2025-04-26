package com.taken_seat.common_service.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public record TicketPerformanceClientResponse(
	String title,
	String name,
	String address,
	String rowNumber,
	String seatNumber,
	String seatType,

	/**
	 * 공연 DB와 도메인에 LocalDateTime 을 사용하고 있어서 동일하게 적용
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm")
	LocalDateTime startAt,

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm")
	LocalDateTime endAt
) {
}