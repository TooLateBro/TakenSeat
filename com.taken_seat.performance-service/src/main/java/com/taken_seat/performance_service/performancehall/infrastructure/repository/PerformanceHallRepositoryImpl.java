package com.taken_seat.performance_service.performancehall.infrastructure.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.taken_seat.performance_service.performancehall.domain.model.PerformanceHall;
import com.taken_seat.performance_service.performancehall.domain.repository.PerformanceHallRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PerformanceHallRepositoryImpl implements PerformanceHallRepository {

	private final PerformanceHallJpaRepository performanceHallJpaRepository;

	@Override
	public PerformanceHall save(PerformanceHall performanceHall) {
		return performanceHallJpaRepository.save(performanceHall);
	}

	@Override
	public Optional<PerformanceHall> findByNameAndAddress(String name, String address) {
		return performanceHallJpaRepository.findByNameAndAddress(name, address);
	}
}
