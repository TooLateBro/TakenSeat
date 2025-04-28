package com.taken_seat.performance_service.performance.presentation.dto.request;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.taken_seat.performance_service.performance.domain.model.PerformanceScheduleStatus;
import com.taken_seat.performance_service.performance.presentation.dto.request.schema.UpdatePerformanceScheduleSchema;

public record UpdatePerformanceScheduleDto(
	UUID performanceScheduleId,
	UUID performanceHallId,
	LocalDateTime startAt,
	LocalDateTime endAt,
	LocalDateTime saleStartAt,
	LocalDateTime saleEndAt,
	PerformanceScheduleStatus status,
	List<UpdateScheduleSeatDto> scheduleSeats
) implements UpdatePerformanceScheduleSchema {
}

