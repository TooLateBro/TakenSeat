package com.taken_seat.performance_service.performance.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.taken_seat.performance_service.performance.domain.model.PerformanceScheduleStatus;

public record PerformanceScheduleResponseDto(
	UUID performanceScheduleId,
	UUID performanceHallId,
	LocalDateTime startAt,
	LocalDateTime endAt,
	LocalDateTime saleStartAt,
	LocalDateTime saleEndAt,
	PerformanceScheduleStatus status,
	List<ScheduleSeatResponseDto> seatPrices
) {
}

