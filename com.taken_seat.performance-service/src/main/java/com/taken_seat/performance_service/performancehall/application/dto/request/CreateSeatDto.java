package com.taken_seat.performance_service.performancehall.application.dto.request;

import com.taken_seat.performance_service.performancehall.domain.model.SeatStatus;
import com.taken_seat.performance_service.performancehall.domain.model.SeatType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateSeatDto implements BaseSeatDto {

	@NotBlank(message = "좌석 열 정보는 필수입니다.")
	@Pattern(regexp = "^[A-Z]{1,2}$", message = "좌석 열(rowNumber)은 A~Z 또는 AA~ZZ 형식이어야 합니다.")
	private String rowNumber;

	@NotBlank(message = "좌석 번호는 필수입니다.")
	@Pattern(
		regexp = "^(?!0{1,5}$)\\d{1,5}$",
		message = "좌석 번호는 1~99999 사이의 숫자 형식이어야 하며, 0으로만 이루어질 수 없습니다."
	)
	private String seatNumber;

	@NotNull(message = "좌석 등급은 필수입니다.")
	private SeatType seatType;

	private SeatStatus status;
}
