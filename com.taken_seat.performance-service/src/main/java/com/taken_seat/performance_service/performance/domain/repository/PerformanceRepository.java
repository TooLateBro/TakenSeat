package com.taken_seat.performance_service.performance.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.taken_seat.performance_service.performance.domain.model.Performance;
import com.taken_seat.performance_service.performance.presentation.dto.request.SearchFilterParam;
import com.taken_seat.performance_service.performance.presentation.dto.response.SearchResponseDto;

public interface PerformanceRepository {

	Performance save(Performance performance);

	Optional<Performance> findById(UUID id);

	Page<SearchResponseDto> findAll(SearchFilterParam filterParam, Pageable pageable);

	Optional<Performance> findByPerformanceScheduleId(UUID performanceScheduleId);
}
