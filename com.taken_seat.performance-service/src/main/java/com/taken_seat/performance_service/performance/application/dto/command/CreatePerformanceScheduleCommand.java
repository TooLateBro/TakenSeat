package com.taken_seat.performance_service.performance.application.dto.command;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CreatePerformanceScheduleCommand(
	UUID performanceHallId,
	LocalDateTime startAt,
	LocalDateTime endAt,
	LocalDateTime saleStartAt,
	LocalDateTime saleEndAt,
	List<CreateScheduleSeatCommand> scheduleSeats
) {
}
