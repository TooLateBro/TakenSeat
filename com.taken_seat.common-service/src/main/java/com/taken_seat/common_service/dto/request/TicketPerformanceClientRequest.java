package com.taken_seat.common_service.dto.request;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TicketPerformanceClientRequest {

	private final UUID performanceId;
	private final UUID performanceScheduleId;
	private final UUID seatId;
}