package com.taken_seat.performance_service.performancehall.domain.repository;

import java.util.Optional;
import java.util.UUID;

import com.taken_seat.performance_service.performancehall.domain.model.PerformanceHall;

public interface PerformanceHallRepository {

	boolean existsByNameAndAddress(String name, String address);

	PerformanceHall save(PerformanceHall performanceHall);

	Optional<PerformanceHall> findById(UUID id);
}
