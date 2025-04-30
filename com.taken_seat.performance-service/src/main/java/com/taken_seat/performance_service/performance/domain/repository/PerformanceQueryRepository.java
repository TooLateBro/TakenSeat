package com.taken_seat.performance_service.performance.domain.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.taken_seat.performance_service.performance.presentation.dto.request.SearchFilterParam;
import com.taken_seat.performance_service.performance.presentation.dto.response.SearchResponseDto;

public interface PerformanceQueryRepository {

	Page<SearchResponseDto> searchByFilter(SearchFilterParam searchFilterParam, Pageable pageable);

	List<UUID> findAllPerformanceScheduleIds();
}


