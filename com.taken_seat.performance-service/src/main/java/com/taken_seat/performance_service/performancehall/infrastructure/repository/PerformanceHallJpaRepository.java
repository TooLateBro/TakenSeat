package com.taken_seat.performance_service.performancehall.infrastructure.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.taken_seat.performance_service.performancehall.domain.model.PerformanceHall;

import feign.Param;

public interface PerformanceHallJpaRepository
	extends JpaRepository<PerformanceHall, UUID>, JpaSpecificationExecutor<PerformanceHall> {

	boolean existsByNameAndAddress(String name, String address);

	Optional<PerformanceHall> findByIdAndDeletedAtIsNull(UUID id);

	boolean existsByNameAndAddressAndIdNot(String name, String address, UUID id);

	@Query("""
		   SELECT ph FROM PerformanceHall ph
		   JOIN ph.seats s
		   WHERE s.id = :seatId
		""")
	Optional<PerformanceHall> findBySeatId(@Param("seatId") UUID seatId);
}
