package com.taken_seat.performance_service.performance.presentation.dto.request;

import java.time.LocalDateTime;
import java.util.UUID;

public record RecommendedPerformanceDto(
	UUID performanceId,
	String title,
	LocalDateTime StartAt
) {
}
