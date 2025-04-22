package com.taken_seat.performance_service.performance.infrastructure.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.taken_seat.performance_service.performance.application.dto.request.SearchFilterParam;
import com.taken_seat.performance_service.performance.application.dto.response.SearchResponseDto;
import com.taken_seat.performance_service.performance.domain.model.Performance;
import com.taken_seat.performance_service.performance.domain.repository.PerformanceQueryRepository;
import com.taken_seat.performance_service.performance.domain.repository.PerformanceRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PerformanceRepositoryImpl implements PerformanceRepository {

	private final PerformanceJpaRepository performanceJpaRepository;
	private final PerformanceQueryRepository performanceQueryRepository;

	@Override
	public Performance save(Performance performance) {
		return performanceJpaRepository.save(performance);
	}

	@Override
	public Optional<Performance> findById(UUID id) {
		return performanceJpaRepository.findByIdAndDeletedAtIsNull(id);
	}

	@Override
	public Page<SearchResponseDto> findAll(SearchFilterParam filterParam, Pageable pageable) {

		return performanceQueryRepository.searchByFilter(filterParam, pageable);
	}

	@Override
	public Optional<Performance> findByPerformanceScheduleId(UUID performanceScheduleId) {
		return performanceJpaRepository.findAllByDeletedAtIsNull().stream()
			.filter(performance -> performance.getSchedules().stream()
				.anyMatch(performanceSchedule -> performanceSchedule.getId().equals(performanceScheduleId)))
			.findFirst();
	}
}
