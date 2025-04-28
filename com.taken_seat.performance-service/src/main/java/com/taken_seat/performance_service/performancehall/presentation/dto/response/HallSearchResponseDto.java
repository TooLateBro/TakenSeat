package com.taken_seat.performance_service.performancehall.presentation.dto.response;

import java.util.UUID;

import com.taken_seat.performance_service.performancehall.presentation.dto.response.schema.HallSearchResponseSchema;

public record HallSearchResponseDto(
	UUID performanceHallId,
	String name,
	Integer totalSeats
) implements HallSearchResponseSchema {
}
