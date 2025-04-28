package com.taken_seat.performance_service.performancehall.presentation.dto.request;

import java.util.List;
import java.util.UUID;

import com.taken_seat.performance_service.performancehall.presentation.dto.request.schema.HallUpdateRequestSchema;

import jakarta.validation.Valid;

public record HallUpdateRequestDto(
	UUID performanceHallId,
	String name,
	String address,
	String description,

	@Valid
	List<HallUpdateSeatDto> seats
) implements HallUpdateRequestSchema {
}
