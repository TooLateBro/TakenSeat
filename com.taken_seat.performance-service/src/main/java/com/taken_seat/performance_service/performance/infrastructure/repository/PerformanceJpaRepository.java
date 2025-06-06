package com.taken_seat.performance_service.performance.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taken_seat.performance_service.performance.domain.model.Performance;

public interface PerformanceJpaRepository
	extends JpaRepository<Performance, UUID> {

	Optional<Performance> findByIdAndDeletedAtIsNull(UUID id);

	List<Performance> findAllByDeletedAtIsNull();
}
