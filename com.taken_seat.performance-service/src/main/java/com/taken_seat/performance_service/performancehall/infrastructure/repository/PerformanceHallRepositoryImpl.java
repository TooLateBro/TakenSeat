package com.taken_seat.performance_service.performancehall.infrastructure.repository;

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
	public boolean existsByNameAndAddress(String name, String address) {
		return performanceHallJpaRepository.existsByNameAndAddress(name, address);
	}
}
