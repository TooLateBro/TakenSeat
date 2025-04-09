package com.taken_seat.performance_service.performance.application.dto.request;

import java.time.LocalDateTime;
import java.util.List;

import com.taken_seat.performance_service.performance.domain.model.PerformanceStatus;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class CreateRequestDto {

	@NotNull(message = "공연 제목은 필수입니다.")
	private String title;

	@NotNull(message = "공연 설명은 필수입니다.")
	private String description;

	@NotNull(message = "공연 시작일을 입력해주세요.")
	@FutureOrPresent
	private LocalDateTime startAt;

	@NotNull(message = "공연 종료일을 입력해주세요.")
	@Future
	private LocalDateTime endAt;

	private PerformanceStatus status;

	private String posterUrl;

	private String ageLimit;

	private Integer maxTicketCount;

	private String discountInfo;

	@NotEmpty(message = "공연 회차 정보는 최소 1개 이상이어야 합니다.")
	private List<CreatePerformanceScheduleDto> schedules;
}
