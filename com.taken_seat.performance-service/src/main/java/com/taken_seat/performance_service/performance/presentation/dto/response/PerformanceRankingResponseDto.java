package com.taken_seat.performance_service.performance.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record PerformanceRankingResponseDto(
	UUID performanceId,
	String title,
	String posterUrl,
	LocalDateTime startAt,
	LocalDateTime endAt
) {
}
