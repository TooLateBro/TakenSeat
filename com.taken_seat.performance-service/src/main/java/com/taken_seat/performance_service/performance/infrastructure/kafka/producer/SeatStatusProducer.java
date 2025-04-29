package com.taken_seat.performance_service.performance.infrastructure.kafka.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SeatStatusProducer {

	private final KafkaTemplate<String, SeatStatusChangedEvent> kafkaTemplate;
	private static final String TOPIC = "seat-status-topic";

	public void sendSeatStatusChangedEvent(SeatStatusChangedEvent seatStatusChangedEvent) {

		kafkaTemplate.send(
			TOPIC,
			seatStatusChangedEvent.scheduleSeatId().toString(),
			seatStatusChangedEvent);

		log.info("[Kafka] SeatStatusChangedEvent 발행 - scheduleSeatId={}, seatStatus={}",
			seatStatusChangedEvent.scheduleSeatId(), seatStatusChangedEvent.seatStatus());
	}
}
