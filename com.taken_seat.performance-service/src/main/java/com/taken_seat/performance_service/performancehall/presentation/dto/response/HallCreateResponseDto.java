package com.taken_seat.performance_service.performancehall.presentation.dto.response;

import java.util.List;
import java.util.UUID;

import com.taken_seat.performance_service.performancehall.presentation.dto.response.schema.HallCreateResponseSchema;

public record HallCreateResponseDto(
	UUID performanceHallId,
	String name,
	String address,
	Integer totalSeats,
	String description,
	List<HallSeatDto> seats
) implements HallCreateResponseSchema {
}
