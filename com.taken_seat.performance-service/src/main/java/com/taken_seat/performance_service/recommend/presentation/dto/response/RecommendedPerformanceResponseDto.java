package com.taken_seat.performance_service.recommend.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record RecommendedPerformanceResponseDto(
	UUID performanceId,
	String title,
	LocalDateTime StartAt
) {
}
