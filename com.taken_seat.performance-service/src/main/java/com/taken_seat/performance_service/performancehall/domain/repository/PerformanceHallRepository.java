package com.taken_seat.performance_service.performancehall.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.taken_seat.performance_service.performancehall.domain.model.PerformanceHall;
import com.taken_seat.performance_service.performancehall.presentation.dto.request.HallSearchFilterParam;
import com.taken_seat.performance_service.performancehall.presentation.dto.response.HallSearchResponseDto;

public interface PerformanceHallRepository {

	boolean existsByNameAndAddress(String name, String address);

	PerformanceHall save(PerformanceHall performanceHall);

	Optional<PerformanceHall> findById(UUID id);

	Page<HallSearchResponseDto> findAll(HallSearchFilterParam filterParam, Pageable pageable);

	boolean existsByNameAndAddressAndIdNot(String name, String address, UUID id);
}