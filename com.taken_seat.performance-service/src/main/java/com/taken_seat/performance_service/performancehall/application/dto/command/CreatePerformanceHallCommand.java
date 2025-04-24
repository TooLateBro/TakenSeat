package com.taken_seat.performance_service.performancehall.application.dto.command;

import java.util.List;

public record CreatePerformanceHallCommand(
	String name,
	String address,
	Integer totalSeats,
	String description,
	List<CreateSeatCommand> seats
) {
}
