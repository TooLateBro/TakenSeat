package com.taken_seat.performance_service.performancehall.application.dto.response;

import java.util.UUID;

import com.taken_seat.performance_service.performancehall.domain.model.SeatStatus;
import com.taken_seat.performance_service.performancehall.domain.model.SeatType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SeatDto {

	private UUID seatId;
	private String rowNumber;
	private String seatNumber;
	private SeatType seatType;
	private SeatStatus status;
}
