package com.taken_seat.performance_service.performancehall.presentation.dto.request;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;

public record UpdateRequestDto(
	UUID performanceHallId,
	String name,
	String address,
	String description,

	@Valid
	List<UpdateSeatDto> seats
) {
}
