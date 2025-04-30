package com.taken_seat.booking_service.booking.presentation.dto.request;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor
@Schema(description = "예매 생성 DTO")
public class BookingCreateRequest {

	@Schema(description = "공연 ID")
	@NotNull(message = "공연 ID는 필수값입니다.")
	private UUID performanceId;

	@Schema(description = "공연 회차 ID")
	@NotNull(message = "공연 회차 ID는 필수값입니다.")
	private UUID performanceScheduleId;

	@Schema(description = "좌석 ID")
	@NotNull(message = "좌석 ID는 필수값입니다.")
	private UUID scheduleSeatId;
}