package com.taken_seat.performance_service.performance.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.taken_seat.performance_service.performance.application.dto.request.SearchFilterParam;
import com.taken_seat.performance_service.performance.domain.model.Performance;

public interface PerformanceRepository {

	Performance save(Performance performance);

	Optional<Performance> findById(UUID id);

	Page<Performance> findAll(SearchFilterParam filterParam, Pageable pageable);

	Optional<Performance> findByPerformanceScheduleId(UUID performanceScheduleId);
}
