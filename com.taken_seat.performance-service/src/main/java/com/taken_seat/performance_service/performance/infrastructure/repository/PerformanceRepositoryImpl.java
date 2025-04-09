package com.taken_seat.performance_service.performance.infrastructure.repository;

import org.springframework.stereotype.Repository;

import com.taken_seat.performance_service.performance.domain.model.Performance;
import com.taken_seat.performance_service.performance.domain.repository.PerformanceRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PerformanceRepositoryImpl implements PerformanceRepository {

	private final PerformanceJpaRepository performanceJpaRepository;

	@Override
	public Performance save(Performance performance) {
		return performanceJpaRepository.save(performance);
	}
}
