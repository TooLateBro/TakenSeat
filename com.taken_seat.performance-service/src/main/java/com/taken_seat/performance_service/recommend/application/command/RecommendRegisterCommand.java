package com.taken_seat.performance_service.recommend.application.command;

import java.util.UUID;

public record RecommendRegisterCommand(
	UUID userId,
	UUID performanceId,
	int ticketCount
) {
}
