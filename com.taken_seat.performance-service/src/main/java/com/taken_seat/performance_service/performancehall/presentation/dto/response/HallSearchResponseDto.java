package com.taken_seat.performance_service.performancehall.presentation.dto.response;

import java.util.UUID;

public record HallSearchResponseDto(
	UUID performanceHallId,
	String name,
	Integer totalSeats
) {
}
