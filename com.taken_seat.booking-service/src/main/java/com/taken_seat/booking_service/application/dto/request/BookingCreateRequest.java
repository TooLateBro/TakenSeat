package com.taken_seat.booking_service.application.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingCreateRequest {
	@Builder.Default
	@NotNull(message = "공연 회차 ID는 필수값입니다.")
	private UUID performanceScheduleId = UUID.randomUUID();

	@Builder.Default
	@NotNull(message = "좌석 ID는 필수값입니다.")
	private UUID seatId = UUID.randomUUID();
}