package com.taken_seat.performance_service.performance.application.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.taken_seat.performance_service.performance.domain.model.PerformanceStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DetailResponseDto {

	private UUID performanceId;
	private String title;
	private String description;
	private LocalDateTime startAt;
	private LocalDateTime endAt;
	private PerformanceStatus status;
	private String posterUrl;
	private String ageLimit;
	private Integer maxTicketCount;
	private String discountInfo;
	private List<PerformanceScheduleResponseDto> schedules;
}
