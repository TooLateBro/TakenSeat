package com.taken_seat.performance_service.performance.presentation.dto.response;

import java.util.List;

import com.taken_seat.performance_service.performance.presentation.dto.response.schema.PageResponseSchema;

public record PageResponseDto(
	List<SearchResponseDto> content,
	int pageSize,
	int pageNumber,
	int totalPages,
	long totalElements,
	boolean isLast
) implements PageResponseSchema {
}
