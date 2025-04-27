package com.taken_seat.booking_service.booking.presentation.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor
public class BookingCreateRequest {
	@NotNull(message = "공연 ID는 필수값입니다.")
	private UUID performanceId;

	@NotNull(message = "공연 회차 ID는 필수값입니다.")
	private UUID performanceScheduleId;

	@NotNull(message = "좌석 ID는 필수값입니다.")
	private UUID scheduleSeatId;
}