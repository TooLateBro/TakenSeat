package com.taken_seat.booking_service.booking.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Schema(description = "예매 취소 DTO")
public class BookingCancelRequest {

	@Schema(description = "취소 사유")
	@NotBlank(message = "취소 사유는 필수 입력값 입니다.")
	private String cancelReason;
}