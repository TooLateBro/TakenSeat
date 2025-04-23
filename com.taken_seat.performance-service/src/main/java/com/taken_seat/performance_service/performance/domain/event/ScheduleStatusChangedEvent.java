package com.taken_seat.performance_service.performance.domain.event;

import java.util.UUID;

public record ScheduleStatusChangedEvent(
	UUID performanceId
) {
}
