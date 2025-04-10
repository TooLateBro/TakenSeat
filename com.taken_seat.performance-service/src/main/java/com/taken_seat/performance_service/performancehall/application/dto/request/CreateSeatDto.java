package com.taken_seat.performance_service.performancehall.application.dto.request;

import com.taken_seat.performance_service.performancehall.domain.model.SeatStatus;
import com.taken_seat.performance_service.performancehall.domain.model.SeatType;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateSeatDto {

	@NotBlank(message = "좌석 열 정보는 필수입니다.")
	private String rowNumber;

	@NotBlank(message = "좌석 번호는 필수입니다.")
	private String seatNumber;

	private SeatType seatType;
	private SeatStatus status;
}
