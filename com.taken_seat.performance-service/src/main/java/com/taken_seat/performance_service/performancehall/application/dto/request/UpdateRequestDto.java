package com.taken_seat.performance_service.performancehall.application.dto.request;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRequestDto {

	private UUID performanceHallId;
	private String name;
	private String address;
	private String description;

	@Valid
	private List<UpdateSeatDto> seats;
}
