package com.taken_seat.performance_service.performance.presentation.dto.request.schema;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.taken_seat.performance_service.performance.domain.model.PerformanceScheduleStatus;
import com.taken_seat.performance_service.performance.presentation.dto.request.UpdateScheduleSeatDto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "UpdatePerformanceSchedule", description = "공연 회차 수정 요청 DTO")
public interface UpdatePerformanceScheduleSchema {
	@Schema(
		description = "공연 회차 ID (UUID)",
		example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
	UUID performanceScheduleId();

	@Schema(
		description = "공연장 ID (UUID)",
		example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
	UUID performanceHallId();

	@Schema(
		description = "회차 시작 일시 (yyyy-MM-dd HH:mm:ss)",
		example = "2025-06-01 19:00:00")
	LocalDateTime startAt();

	@Schema(
		description = "회차 종료 일시 (yyyy-MM-dd HH:mm:ss)",
		example = "2025-06-01 21:30:00")
	LocalDateTime endAt();

	@Schema(
		description = "예매 시작 일시 (yyyy-MM-dd HH:mm:ss)",
		example = "2025-05-01 10:00:00")
	LocalDateTime saleStartAt();

	@Schema(
		description = "예매 종료 일시 (yyyy-MM-dd HH:mm:ss)",
		example = "2025-05-31 23:59:59")
	LocalDateTime saleEndAt();

	@Schema(
		description = "회차 상태 (CREATED, OPEN, CLOSED 등)",
		example = "CREATED")
	PerformanceScheduleStatus status();

	@Schema(description = "수정할 좌석 정보 리스트")
	List<UpdateScheduleSeatDto> scheduleSeats();
}
