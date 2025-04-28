package com.taken_seat.performance_service.performancehall.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.taken_seat.performance_service.performancehall.presentation.dto.request.HallSearchFilterParam;
import com.taken_seat.performance_service.performancehall.presentation.dto.response.HallSearchResponseDto;

public interface PerformanceHallQueryRepository {

	Page<HallSearchResponseDto> searchByFilter(HallSearchFilterParam hallSearchFilterParam, Pageable pageable);
}
