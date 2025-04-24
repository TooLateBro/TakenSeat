package com.taken_seat.performance_service.performancehall.presentation.dto.response;

import java.util.UUID;

public record SearchResponseDto(
	UUID performanceHallId,
	String name,
	Integer totalSeats
) {
}
