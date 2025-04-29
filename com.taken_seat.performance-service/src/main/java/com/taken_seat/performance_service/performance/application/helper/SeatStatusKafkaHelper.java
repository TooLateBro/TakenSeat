package com.taken_seat.performance_service.performance.application.helper;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.taken_seat.performance_service.performance.infrastructure.kafka.producer.SeatStatusChangedEvent;
import com.taken_seat.performance_service.performance.infrastructure.kafka.producer.SeatStatusProducer;
import com.taken_seat.performance_service.performancehall.domain.model.SeatStatus;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SeatStatusKafkaHelper {

	private final SeatStatusProducer seatStatusProducer;

	public void sendSeatStatusChangedEvent(UUID performanceScheduleId, UUID scheduleSeatId, SeatStatus seatStatus) {

		SeatStatusChangedEvent event = new SeatStatusChangedEvent(performanceScheduleId, scheduleSeatId, seatStatus);
		seatStatusProducer.sendSeatStatusChangedEvent(event);
	}
}
