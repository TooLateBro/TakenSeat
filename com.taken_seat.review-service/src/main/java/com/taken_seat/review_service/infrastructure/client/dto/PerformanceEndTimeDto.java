package com.taken_seat.review_service.infrastructure.client.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public record PerformanceEndTimeDto(
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	LocalDateTime endAt) {
}
