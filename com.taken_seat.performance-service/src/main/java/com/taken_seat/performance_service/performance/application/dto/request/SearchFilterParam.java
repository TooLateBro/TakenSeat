package com.taken_seat.performance_service.performance.application.dto.request;

import java.time.LocalDateTime;

import com.taken_seat.performance_service.performance.domain.model.PerformanceStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchFilterParam {

	private String title;
	private LocalDateTime startAt;
	private LocalDateTime endAt;
	private PerformanceStatus status;
}
