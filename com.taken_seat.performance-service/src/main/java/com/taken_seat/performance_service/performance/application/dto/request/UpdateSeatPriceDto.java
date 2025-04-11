package com.taken_seat.performance_service.performance.application.dto.request;

import java.util.UUID;

import com.taken_seat.performance_service.performancehall.domain.model.SeatType;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class UpdateSeatPriceDto {

	private UUID performanceSeatPriceId;
	private SeatType seatType;

	@Positive(message = "좌석 가격은 0보다 커야 합니다.")
	private Integer price;
}
