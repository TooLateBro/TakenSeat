package com.taken_seat.performance_service.performancehall.presentation.dto.response;

import java.util.List;
import java.util.UUID;

import com.taken_seat.performance_service.performancehall.presentation.dto.response.schema.HallUpdateResponseSchema;

public record HallUpdateResponseDto(
	UUID performanceHallId,
	String name,
	String address,
	Integer totalSeats,
	String description,
	List<HallSeatDto> seats
) implements HallUpdateResponseSchema {
}
