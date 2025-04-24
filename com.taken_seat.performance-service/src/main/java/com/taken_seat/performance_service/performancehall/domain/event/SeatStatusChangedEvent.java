package com.taken_seat.performance_service.performancehall.domain.event;

import java.util.UUID;

import com.taken_seat.performance_service.performancehall.domain.model.SeatStatus;

public record SeatStatusChangedEvent(
	UUID performanceId,
	UUID performanceScheduleId,
	UUID seatId,
	SeatStatus newStatus
) {
}
