package com.taken_seat.performance_service.performance.presentation.dto.response.schema;

import java.util.List;

import com.taken_seat.performance_service.performance.presentation.dto.response.ScheduleSeatResponseDto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "SeatLayoutResponse", description = "좌석 배치도 응답 DTO")
public interface SeatLayoutResponseSchema {

	@Schema(description = "스케줄 좌석 응답 정보 리스트")
	List<ScheduleSeatResponseDto> scheduleSeats();
}