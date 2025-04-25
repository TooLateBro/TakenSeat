package com.taken_seat.performance_service.performance.presentation.dto.request;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.taken_seat.performance_service.performance.domain.model.PerformanceScheduleStatus;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CreatePerformanceScheduleDto(
	@NotNull(message = "공연장 ID는 필수입니다.")
	UUID performanceHallId,

	@NotNull(message = "회차 시작일을 입력해주세요.")
	@Future
	LocalDateTime startAt,

	@NotNull(message = "회차 종료일을 입력해주세요.")
	@Future
	LocalDateTime endAt,

	@NotNull(message = "예매 시작일은 필수입니다.")
	@FutureOrPresent
	LocalDateTime saleStartAt,

	@Future
	LocalDateTime saleEndAt,

	PerformanceScheduleStatus status,

	@NotEmpty(message = "좌석 가격 정보는 최소 1개 이상이어야 합니다.")
	List<CreateScheduleSeatDto> seatPrices
) {
}

