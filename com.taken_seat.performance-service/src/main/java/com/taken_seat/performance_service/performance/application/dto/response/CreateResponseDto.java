package com.taken_seat.performance_service.performance.application.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.taken_seat.performance_service.performance.domain.model.PerformanceStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class CreateResponseDto {

	private UUID performanceId;
	private String title;
	private String description;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime startAt;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime endAt;

	private PerformanceStatus status;
	private String posterUrl;
	private String ageLimit;
	private Integer maxTicketCount;
	private String discountInfo;
	private List<PerformanceScheduleResponseDto> schedules;
}
