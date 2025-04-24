package com.taken_seat.performance_service.performancehall.infrastructure.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.taken_seat.performance_service.performancehall.domain.model.PerformanceHall;
import com.taken_seat.performance_service.performancehall.domain.repository.PerformanceHallQueryRepository;
import com.taken_seat.performance_service.performancehall.domain.repository.PerformanceHallRepository;
import com.taken_seat.performance_service.performancehall.presentation.dto.request.SearchFilterParam;
import com.taken_seat.performance_service.performancehall.presentation.dto.response.SearchResponseDto;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PerformanceHallRepositoryImpl implements PerformanceHallRepository {

	private final PerformanceHallJpaRepository performanceHallJpaRepository;
	private final PerformanceHallQueryRepository performanceHallQueryRepository;

	@Override
	public PerformanceHall save(PerformanceHall performanceHall) {
		return performanceHallJpaRepository.save(performanceHall);
	}

	@Override
	public boolean existsByNameAndAddress(String name, String address) {
		return performanceHallJpaRepository.existsByNameAndAddress(name, address);
	}

	@Override
	public Optional<PerformanceHall> findById(UUID id) {
		return performanceHallJpaRepository.findByIdAndDeletedAtIsNull(id);
	}

	@Override
	public Page<SearchResponseDto> findAll(SearchFilterParam filterParam, Pageable pageable) {
		return performanceHallQueryRepository.searchByFilter(filterParam, pageable);
	}

	@Override
	public boolean existsByNameAndAddressAndIdNot(String name, String address, UUID id) {
		return performanceHallJpaRepository.existsByNameAndAddressAndIdNot(name, address, id);
	}

	@Override
	public Optional<PerformanceHall> findBySeatId(UUID seatId) {
		return performanceHallJpaRepository.findBySeatId(seatId);
	}

	@Override
	public PerformanceHall saveAndFlush(PerformanceHall performanceHall) {
		return performanceHallJpaRepository.saveAndFlush(performanceHall);
	}
}