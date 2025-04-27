package com.taken_seat.performance_service.performance.application.dto.command;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.taken_seat.performance_service.performance.domain.model.PerformanceScheduleStatus;

public record UpdatePerformanceScheduleCommand(
	UUID performanceScheduleId,
	UUID performanceHallId,
	LocalDateTime startAt,
	LocalDateTime endAt,
	LocalDateTime saleStartAt,
	LocalDateTime saleEndAt,
	PerformanceScheduleStatus status,
	List<UpdateScheduleSeatCommand> scheduleSeats
) {
}

