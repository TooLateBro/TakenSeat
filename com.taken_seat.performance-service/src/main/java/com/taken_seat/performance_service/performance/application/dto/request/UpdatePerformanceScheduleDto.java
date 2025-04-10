package com.taken_seat.performance_service.performance.application.dto.request;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.taken_seat.performance_service.performance.domain.model.PerformanceScheduleStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class UpdatePerformanceScheduleDto {

	private UUID performanceScheduleId;
	private UUID performanceHallId;
	private LocalDateTime startAt;
	private LocalDateTime endAt;
	private LocalDateTime saleStartAt;
	private LocalDateTime saleEndAt;
	private PerformanceScheduleStatus status;
	private List<UpdateSeatPriceDto> seatPrices;
}

