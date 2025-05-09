package com.taken_seat.review_service.application.service;

import java.util.List;
import java.util.UUID;

public interface ReviewChangeMaker {
	void markPerformanceChanged(UUID performanceId);

	List<UUID> getChangedPerformanceIds();

	void clearChangedPerformanceIds();
}
