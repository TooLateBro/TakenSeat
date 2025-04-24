package com.taken_seat.performance_service.performance.application.dto.command;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.taken_seat.performance_service.performance.domain.model.PerformanceStatus;

public record UpdatePerformanceCommand(
	UUID performanceId,
	String title,
	String description,
	LocalDateTime startAt,
	LocalDateTime endAt,
	PerformanceStatus status,
	String posterUrl,
	String ageLimit,
	Integer maxTicketCount,
	String discountInfo,
	List<UpdatePerformanceScheduleCommand> schedules
) {
}