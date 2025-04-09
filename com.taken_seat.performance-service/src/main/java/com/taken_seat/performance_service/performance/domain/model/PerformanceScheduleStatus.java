package com.taken_seat.performance_service.performance.domain.model;

import java.time.LocalDateTime;

public enum PerformanceScheduleStatus {

	PENDING,
	ONSALE,
	CLOSED,
	SOLDOUT;

	public static PerformanceScheduleStatus status(LocalDateTime startAt, LocalDateTime endAt, boolean isSoldOut) {

		LocalDateTime now = LocalDateTime.now();

		if (now.isBefore(startAt)) {
			return PENDING;
		}

		if (!now.isAfter(endAt)) {
			return isSoldOut ? SOLDOUT : ONSALE;
		}

		return CLOSED;
	}
}
