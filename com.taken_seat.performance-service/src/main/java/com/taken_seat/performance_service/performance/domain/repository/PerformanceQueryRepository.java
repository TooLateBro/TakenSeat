package com.taken_seat.performance_service.performance.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.taken_seat.performance_service.performance.application.dto.request.SearchFilterParam;
import com.taken_seat.performance_service.performance.application.dto.response.SearchResponseDto;

public interface PerformanceQueryRepository {

	Page<SearchResponseDto> searchByFilter(SearchFilterParam searchFilterParam, Pageable pageable);
}


