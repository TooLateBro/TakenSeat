package com.taken_seat.common_service.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TicketPerformanceClientResponse {

	private final String title;
	private final String name;
	private final String address;
	private final String seatRowNumber;
	private final String seatNumber;
	private final String seatType;

	/**
	 * 공연 DB와 도메인에 LocalDateTime 을 사용하고 있어서 동일하게 적용
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm")
	private LocalDateTime startAt;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm")
	private LocalDateTime endAt;
}