package com.taken_seat.performance_service.performancehall.presentation.dto.response;

import java.util.List;

public record SeatLayoutResponseDto(
	List<SeatDto> seats
) {
}
