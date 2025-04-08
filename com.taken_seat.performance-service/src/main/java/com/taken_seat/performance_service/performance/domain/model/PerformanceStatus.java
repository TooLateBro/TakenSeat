package com.taken_seat.performance_service.performance.domain.model;

import java.time.LocalDateTime;

public enum PerformanceStatus {

	UPCOMING,
	LIVE,
	CLOSED;

	public static PerformanceStatus status(LocalDateTime startAt, LocalDateTime endAt) {

		LocalDateTime now = LocalDateTime.now();

		if (now.isBefore(startAt)) {
			return UPCOMING;
		}

		if ((now.isEqual(startAt) || now.isAfter(startAt)) && !now.isAfter(endAt)) {
			return LIVE;
		}

		return CLOSED;
	}
}
