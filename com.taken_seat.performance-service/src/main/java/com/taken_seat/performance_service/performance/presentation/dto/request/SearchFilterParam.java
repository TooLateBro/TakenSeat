package com.taken_seat.performance_service.performance.presentation.dto.request;

import java.time.LocalDateTime;

import com.taken_seat.performance_service.performance.domain.model.PerformanceStatus;
import com.taken_seat.performance_service.performance.presentation.dto.request.schema.SearchFilterParamSchema;

public record SearchFilterParam(
	String title,
	LocalDateTime startAt,
	LocalDateTime endAt,
	PerformanceStatus status
) implements SearchFilterParamSchema {
}
