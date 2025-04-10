package com.taken_seat.performance_service.performancehall.domain.repository;

import com.taken_seat.performance_service.performancehall.domain.model.PerformanceHall;

public interface PerformanceHallRepository {

	boolean existsByNameAndAddress(String name, String address);

	PerformanceHall save(PerformanceHall performanceHall);
}
