package com.taken_seat.performance_service.performance.application.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.taken_seat.performance_service.performance.domain.event.ScheduleStatusChangedEvent;
import com.taken_seat.performance_service.performance.domain.model.Performance;
import com.taken_seat.performance_service.performance.domain.model.PerformanceStatus;
import com.taken_seat.performance_service.performance.domain.validator.PerformanceExistenceValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduleStatusChangedEventHandler {

	private final PerformanceExistenceValidator performanceExistenceValidator;

	@EventListener
	@Transactional
	public void handleScheduleStatusChangedEvent(ScheduleStatusChangedEvent scheduleStatusChangedEvent) {

		Performance performance = performanceExistenceValidator.validateByPerformanceId(
			scheduleStatusChangedEvent.performanceId());

		PerformanceStatus newStatus = PerformanceStatus.status(
			performance.getStartAt(),
			performance.getEndAt(),
			performance.getSchedules()
		);

		performance.updateStatus(newStatus);

		log.info(
			"[Performance] 도메인 이벤트 - 공연 상태 변경 완료 → performanceId={}, newStatus={}",
			performance.getId(), newStatus
		);
	}
}
