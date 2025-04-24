package com.taken_seat.performance_service.performancehall.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.taken_seat.performance_service.performancehall.domain.model.PerformanceHall;
import com.taken_seat.performance_service.performancehall.presentation.dto.request.SearchFilterParam;
import com.taken_seat.performance_service.performancehall.presentation.dto.response.SearchResponseDto;

public interface PerformanceHallRepository {

	boolean existsByNameAndAddress(String name, String address);

	PerformanceHall save(PerformanceHall performanceHall);

	Optional<PerformanceHall> findById(UUID id);

	Page<SearchResponseDto> findAll(SearchFilterParam filterParam, Pageable pageable);

	boolean existsByNameAndAddressAndIdNot(String name, String address, UUID id);

	Optional<PerformanceHall> findBySeatId(UUID seatId);

	PerformanceHall saveAndFlush(PerformanceHall performanceHall);
}