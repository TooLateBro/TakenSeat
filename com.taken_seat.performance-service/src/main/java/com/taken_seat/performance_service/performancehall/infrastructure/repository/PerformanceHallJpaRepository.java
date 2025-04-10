package com.taken_seat.performance_service.performancehall.infrastructure.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.taken_seat.performance_service.performancehall.domain.model.PerformanceHall;

public interface PerformanceHallJpaRepository
	extends JpaRepository<PerformanceHall, UUID>, JpaSpecificationExecutor<PerformanceHall> {

	boolean existsByNameAndAddress(String name, String address);

	Optional<PerformanceHall> findByIdAndDeletedAtIsNull(UUID id);
}
