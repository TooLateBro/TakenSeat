package com.taken_seat.performance_service.performancehall.application.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.taken_seat.performance_service.performance.domain.event.ScheduleStatusChangedEvent;
import com.taken_seat.performance_service.performance.domain.facade.PerformanceFacade;
import com.taken_seat.performance_service.performance.domain.model.Performance;
import com.taken_seat.performance_service.performance.domain.model.PerformanceScheduleStatus;
import com.taken_seat.performance_service.performancehall.domain.event.SeatStatusChangedEvent;
import com.taken_seat.performance_service.performancehall.domain.model.SeatStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SeatStatusEventHandler {

	private final PerformanceFacade performanceFacade;
	private final ApplicationEventPublisher applicationEventPublisher;

	@EventListener
	@Transactional
	public void handleSeatStatusChangedEvent(SeatStatusChangedEvent seatStatusChangedEvent) {

		log.info(
			"[Performance] 도메인 이벤트 - 좌석 상태 변경 이벤트 수신 완료 → performanceId={}, scheduleId={}, seatId={}, status={}",
			seatStatusChangedEvent.performanceId(), seatStatusChangedEvent.performanceScheduleId(),
			seatStatusChangedEvent.seatId(), seatStatusChangedEvent.newStatus());

		Performance performance = performanceFacade.getByPerformanceId(
			seatStatusChangedEvent.performanceId()
		);

		if (seatStatusChangedEvent.newStatus() == SeatStatus.SOLDOUT) {
			performance.updateScheduleStatus(
				seatStatusChangedEvent.performanceScheduleId(),
				PerformanceScheduleStatus.SOLDOUT
			);
		} else if (seatStatusChangedEvent.newStatus() == SeatStatus.AVAILABLE) {
			performance.updateScheduleStatus(
				seatStatusChangedEvent.performanceScheduleId(),
				PerformanceScheduleStatus.ONSALE
			);
		}

		applicationEventPublisher.publishEvent(
			new ScheduleStatusChangedEvent(seatStatusChangedEvent.performanceId())
		);
	}
}
