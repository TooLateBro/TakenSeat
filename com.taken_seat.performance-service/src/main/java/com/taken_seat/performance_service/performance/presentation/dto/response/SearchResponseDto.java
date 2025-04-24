package com.taken_seat.performance_service.performance.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.taken_seat.performance_service.performance.domain.model.PerformanceStatus;

public record SearchResponseDto(
	UUID performanceId,
	String title,
	LocalDateTime startAt,
	LocalDateTime endAt,
	PerformanceStatus status,
	String posterUrl
) {
}
