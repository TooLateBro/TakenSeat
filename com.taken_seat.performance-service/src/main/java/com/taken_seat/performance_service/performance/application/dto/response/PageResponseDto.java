package com.taken_seat.performance_service.performance.application.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageResponseDto {

	private List<SearchResponseDto> content;
	private int pageSize;
	private int pageNumber;
	private int totalPages;
	private long totalElements;
	private boolean isLast;
}
