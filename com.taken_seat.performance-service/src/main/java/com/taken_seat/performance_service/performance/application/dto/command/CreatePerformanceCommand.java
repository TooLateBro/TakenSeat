package com.taken_seat.performance_service.performance.application.dto.command;

import java.time.LocalDateTime;
import java.util.List;

public record CreatePerformanceCommand(
	String title,
	String description,
	LocalDateTime startAt,
	LocalDateTime endAt,
	String posterUrl,
	String ageLimit,
	Integer maxTicketCount,
	String discountInfo,
	List<CreatePerformanceScheduleCommand> schedules
) {
}
