package com.taken_seat.performance_service.performancehall.presentation.dto.request;

import java.util.List;

import com.taken_seat.performance_service.performancehall.presentation.dto.request.schema.HallCreateRequestSchema;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record HallCreateRequestDto(
	@NotBlank(message = "공연장 이름은 필수입니다.")
	String name,

	@NotBlank(message = "공연장 주소는 필수입니다.")
	String address,

	@NotNull(message = "공연장 총 좌석수는 필수입니다.")
	Integer totalSeats,

	String description,

	@Valid
	@NotEmpty(message = "공연 좌석 정보는 필수입니다")
	List<HallCreateSeatDto> seats
) implements HallCreateRequestSchema {
}
