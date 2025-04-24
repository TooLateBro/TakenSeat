package com.taken_seat.performance_service.performancehall.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.taken_seat.performance_service.performancehall.application.dto.request.SearchFilterParam;
import com.taken_seat.performance_service.performancehall.application.dto.response.SearchResponseDto;

public interface PerformanceHallQueryRepository {

	Page<SearchResponseDto> searchByFilter(SearchFilterParam searchFilterParam, Pageable pageable);
}
