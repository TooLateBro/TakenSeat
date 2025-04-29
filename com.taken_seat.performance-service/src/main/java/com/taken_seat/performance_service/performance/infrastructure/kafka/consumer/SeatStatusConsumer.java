package com.taken_seat.performance_service.performance.infrastructure.kafka.consumer;

import org.springframework.stereotype.Component;

import com.taken_seat.performance_service.performance.application.service.PerformanceClientService;
import com.taken_seat.performance_service.performance.infrastructure.kafka.producer.SeatStatusChangedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SeatStatusConsumer {

	private final PerformanceClientService performanceClientService;

	public void consumeSeatStatusChangedEvent(SeatStatusChangedEvent event) {

		log.info("[Performance] Kafka SeatStatusChangedEvent 수신 - scheduleSeatId={}, seatStatus={}",
			event.scheduleSeatId(), event.seatStatus());

		performanceClientService.updateScheduleSeatStatusByKafka(
			event.performanceScheduleId(),
			event.scheduleSeatId(),
			event.seatStatus()
		);
	}
}
