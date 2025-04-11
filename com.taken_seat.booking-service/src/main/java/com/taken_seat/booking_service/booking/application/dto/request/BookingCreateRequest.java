package com.taken_seat.booking_service.booking.application.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BookingCreateRequest {
	@NotNull(message = "공연 ID는 필수값입니다.")
	private UUID performanceId;

	@NotNull(message = "공연 회차 ID는 필수값입니다.")
	private UUID performanceScheduleId;

	@NotNull(message = "좌석 ID는 필수값입니다.")
	private UUID seatId;
}