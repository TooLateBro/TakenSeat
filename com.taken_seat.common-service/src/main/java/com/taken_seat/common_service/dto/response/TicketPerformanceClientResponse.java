package com.taken_seat.common_service.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TicketPerformanceClientResponse {
	private final String startAt;
	private final String endAt;
	private final String name;
	private final String address;
	private final String seatRowNumber;
	private final String seatNumber;
	private final String seatType;
}