package com.taken_seat.performance_service.performance.presentation.dto.response;

import java.util.List;

public record SeatLayoutResponseDto(
	List<ScheduleSeatResponseDto> seats
) {
}
