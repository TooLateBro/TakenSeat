package com.taken_seat.performance_service.performance.infrastructure.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.taken_seat.performance_service.performance.domain.model.Performance;

public interface PerformanceJpaRepository
	extends JpaRepository<Performance, UUID>, JpaSpecificationExecutor<Performance> {

	Optional<Performance> findByIdAndDeletedAtIsNull(UUID id);
}
