package com.taken_seat.performance_service.performancehall.presentation.dto.response;

import java.util.List;

public record HallPageResponseDto(
	List<HallSearchResponseDto> content,
	int pageSize,
	int pageNumber,
	int totalPages,
	long totalElements,
	boolean isLast
) {
}
