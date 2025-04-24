package com.taken_seat.performance_service.performancehall.application.dto.command;

import java.util.List;
import java.util.UUID;

public record UpdatePerformanceHallCommand(
	UUID performanceHallId,
	String name,
	String address,
	String description,
	List<UpdateSeatCommand> seats
) {
}
