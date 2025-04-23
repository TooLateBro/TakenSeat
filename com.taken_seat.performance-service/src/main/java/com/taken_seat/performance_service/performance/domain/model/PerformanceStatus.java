package com.taken_seat.performance_service.performance.domain.model;

import java.time.LocalDateTime;
import java.util.List;

public enum PerformanceStatus {

	UPCOMING,
	LIVE,
	SOLDOUT,
	CLOSED;

	public static PerformanceStatus status(
		LocalDateTime startAt,
		LocalDateTime endAt,
		List<PerformanceSchedule> schedules
	) {

		LocalDateTime now = LocalDateTime.now();

		boolean live = (now.isEqual(startAt) || now.isAfter(startAt)) && !now.isAfter(endAt);

		boolean allSchedulesSoldOut =
			schedules != null
				&& !schedules.isEmpty()
				&& schedules.stream().allMatch(schedule -> schedule.getStatus() == PerformanceScheduleStatus.SOLDOUT);

		if (now.isBefore(startAt)) {
			return UPCOMING;
		}

		if (live && allSchedulesSoldOut) {
			return SOLDOUT;
		} else if (live) {
			return LIVE;
		}

		return CLOSED;
	}
}
