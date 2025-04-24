package com.taken_seat.common_service.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor
public class TicketPerformanceClientResponse {

	private String title;
	private String name;
	private String address;
	private String seatRowNumber;
	private String seatNumber;
	private String seatType;

	/**
	 * 공연 DB와 도메인에 LocalDateTime 을 사용하고 있어서 동일하게 적용
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm")
	private LocalDateTime startAt;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm")
	private LocalDateTime endAt;
}