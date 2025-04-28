package com.taken_seat.performance_service.performance.presentation.dto.request;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.taken_seat.performance_service.performance.domain.model.PerformanceStatus;
import com.taken_seat.performance_service.performance.presentation.dto.request.schema.UpdateRequestSchema;

public record UpdateRequestDto(
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
	List<UpdatePerformanceScheduleDto> schedules
) implements UpdateRequestSchema {
}
