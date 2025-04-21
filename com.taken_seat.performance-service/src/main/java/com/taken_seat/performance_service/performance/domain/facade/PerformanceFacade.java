package com.taken_seat.performance_service.performance.domain.facade;

import java.util.UUID;

import com.taken_seat.performance_service.performance.domain.model.Performance;

public interface PerformanceFacade {

	Performance getByPerformanceId(UUID performanceId);

	Performance getByPerformanceScheduleId(UUID performanceId);
}
