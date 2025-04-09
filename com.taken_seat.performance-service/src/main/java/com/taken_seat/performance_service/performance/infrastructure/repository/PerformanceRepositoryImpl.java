package com.taken_seat.performance_service.performance.infrastructure.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.taken_seat.performance_service.performance.application.dto.request.SearchFilterParam;
import com.taken_seat.performance_service.performance.domain.model.Performance;
import com.taken_seat.performance_service.performance.domain.repository.PerformanceRepository;
import com.taken_seat.performance_service.performance.domain.repository.spec.PerformanceSpecification;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PerformanceRepositoryImpl implements PerformanceRepository {

	private final PerformanceJpaRepository performanceJpaRepository;

	@Override
	public Performance save(Performance performance) {
		return performanceJpaRepository.save(performance);
	}

	@Override
	public Optional<Performance> findById(UUID id) {
		return performanceJpaRepository.findByIdAndDeletedAtIsNull(id);
	}

	@Override
	public Page<Performance> findAll(SearchFilterParam filterParam, Pageable pageable) {

		return performanceJpaRepository.findAll(
			PerformanceSpecification.withFilter(filterParam),
			pageable
		);
	}

	@Override
	public void deleteById(UUID id, UUID deletedBy) {

		Performance performance = performanceJpaRepository.findByIdAndDeletedAtIsNull(id)
			.orElseThrow(() -> new IllegalArgumentException("이미 삭제된 아이디입니다"));

		performance.softDelete(deletedBy);
	}
}
