package com.taken_seat.performance_service.performancehall.presentation.dto.response;

import java.util.List;

import com.taken_seat.performance_service.performancehall.presentation.dto.response.schema.HallPageResponseSchema;

public record HallPageResponseDto(
	List<HallSearchResponseDto> content,
	int pageSize,
	int pageNumber,
	int totalPages,
	long totalElements,
	boolean isLast
) implements HallPageResponseSchema {
}
