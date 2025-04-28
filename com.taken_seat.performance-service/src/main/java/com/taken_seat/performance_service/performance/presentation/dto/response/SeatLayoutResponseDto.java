package com.taken_seat.performance_service.performance.presentation.dto.response;

import java.util.List;

import com.taken_seat.performance_service.performance.presentation.dto.response.schema.SeatLayoutResponseSchema;

public record SeatLayoutResponseDto(
	List<ScheduleSeatResponseDto> scheduleSeats
) implements SeatLayoutResponseSchema {
}
