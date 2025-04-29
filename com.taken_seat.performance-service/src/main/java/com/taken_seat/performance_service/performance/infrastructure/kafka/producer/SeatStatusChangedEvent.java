package com.taken_seat.performance_service.performance.infrastructure.kafka.producer;

import java.util.UUID;

import com.taken_seat.performance_service.performancehall.domain.model.SeatStatus;

public record SeatStatusChangedEvent(
	UUID performanceScheduleId,
	UUID scheduleSeatId,
	SeatStatus seatStatus
) {
}
