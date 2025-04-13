package com.taken_seat.performance_service.performancehall.application.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateRequestDto {

	@NotBlank(message = "공연장 이름은 필수입니다.")
	private String name;

	@NotBlank(message = "공연장 주소는 필수입니다.")
	private String address;

	@NotNull(message = "공연장 총 좌석수는 필수입니다.")
	private Integer totalSeats;

	private String description;

	@Valid
	@NotEmpty(message = "공연 좌석 정보는 필수입니다")
	private List<CreateSeatDto> seats;
}
