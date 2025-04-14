package com.taken_seat.performance_service.performance.domain.model;

import java.time.LocalDateTime;

public enum PerformanceScheduleStatus {

	PENDING,
	ONSALE,
	CLOSED,
	SOLDOUT;

	public static PerformanceScheduleStatus status(LocalDateTime saleStartAt, LocalDateTime saleEndAt,
		boolean isSoldOut) {

		LocalDateTime now = LocalDateTime.now();

		if (now.isBefore(saleStartAt)) {
			return PENDING;
		}

		if (!now.isAfter(saleEndAt)) {
			return isSoldOut ? SOLDOUT : ONSALE;
		}

		return CLOSED;
	}
}
