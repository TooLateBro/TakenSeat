package com.taken_seat.performance_service.performance.domain.repository;

import java.util.Optional;
import java.util.UUID;

import com.taken_seat.performance_service.performance.domain.model.Performance;

public interface PerformanceRepository {

	Performance save(Performance performance);

	Optional<Performance> findById(UUID id);

	void deleteById(UUID id, UUID deletedBy);
}
