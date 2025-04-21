package com.taken_seat.performance_service.performancehall.domain.facade;

import java.util.UUID;

import com.taken_seat.performance_service.performancehall.domain.model.PerformanceHall;

public interface PerformanceHallFacade {

	PerformanceHall getByPerformanceHallId(UUID performanceHallId);

	boolean isSoldOut(UUID performanceHallId);
}
