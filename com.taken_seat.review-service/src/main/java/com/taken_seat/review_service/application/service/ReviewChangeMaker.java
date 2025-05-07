package com.taken_seat.review_service.application.service;

import java.util.UUID;

public interface ReviewChangeMaker {
	void markPerformanceChanged(UUID performanceId);
}
