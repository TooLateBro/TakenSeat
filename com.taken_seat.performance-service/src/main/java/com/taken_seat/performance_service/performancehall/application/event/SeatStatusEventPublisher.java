package com.taken_seat.performance_service.performancehall.application.event;

import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.taken_seat.performance_service.performancehall.domain.event.SeatStatusChangedEvent;
import com.taken_seat.performance_service.performancehall.domain.model.SeatStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SeatStatusEventPublisher {

	private final ApplicationEventPublisher applicationEventPublisher;

	public void publish(
		UUID performanceId,
		UUID performanceScheduleId,
		UUID seatId,
		SeatStatus newStatus
	) {

		log.info("[performance] 도메인 이벤트 - 좌석 상태 변경 이벤트 발행 → performanceId={}, scheduleId={}, seatId={}, status={}",
			performanceId, performanceScheduleId, seatId, newStatus);

		SeatStatusChangedEvent event =
			new SeatStatusChangedEvent(performanceId, performanceScheduleId, seatId, newStatus);
		applicationEventPublisher.publishEvent(event);
	}
}
