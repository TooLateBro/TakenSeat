package com.taken_seat.performance_service.performance.presentation.dto.request;

import java.time.LocalDateTime;
import java.util.List;

import com.taken_seat.performance_service.performance.domain.model.PerformanceStatus;
import com.taken_seat.performance_service.performance.presentation.dto.request.schema.CreateRequestSchema;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CreateRequestDto(
	@NotNull(message = "공연 제목은 필수입니다.")
	String title,

	@NotNull(message = "공연 설명은 필수입니다.")
	String description,

	@NotNull(message = "공연 시작일을 입력해주세요.")
	@FutureOrPresent
	LocalDateTime startAt,

	@NotNull(message = "공연 종료일을 입력해주세요.")
	@Future
	LocalDateTime endAt,

	PerformanceStatus status,
	String posterUrl,
	String ageLimit,
	Integer maxTicketCount,
	String discountInfo,

	@NotEmpty(message = "공연 회차 정보는 최소 1개 이상이어야 합니다.")
	List<CreatePerformanceScheduleDto> schedules
) implements CreateRequestSchema {
}
