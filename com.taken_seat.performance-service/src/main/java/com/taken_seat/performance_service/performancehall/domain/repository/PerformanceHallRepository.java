package com.taken_seat.performance_service.performancehall.domain.repository;

import java.util.Optional;

import com.taken_seat.performance_service.performancehall.domain.model.PerformanceHall;

public interface PerformanceHallRepository {

	Optional<PerformanceHall> findByNameAndAddress(String name, String address);

	PerformanceHall save(PerformanceHall performanceHall);
}
