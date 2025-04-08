package com.taken_seat.performance_service.performance.application.dto.response;

import java.util.UUID;

import com.taken_seat.performance_service.performancehall.domain.model.SeatType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class SeatPriceResponseDto {

	private UUID PerformanceSeatPriceId;
	private SeatType seatType;
	private Integer price;
}
