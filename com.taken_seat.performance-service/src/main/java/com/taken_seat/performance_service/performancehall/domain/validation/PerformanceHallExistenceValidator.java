package com.taken_seat.performance_service.performancehall.domain.validation;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.taken_seat.common_service.exception.customException.PerformanceException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import com.taken_seat.performance_service.performancehall.domain.model.PerformanceHall;
import com.taken_seat.performance_service.performancehall.domain.repository.PerformanceHallRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PerformanceHallExistenceValidator {

	private final PerformanceHallRepository performanceHallRepository;

	public PerformanceHall validateByPerformanceHallId(UUID performanceHallId) {
		return performanceHallRepository.findById(performanceHallId)
			.orElseThrow(() -> new PerformanceException(ResponseCode.PERFORMANCE_HALL_NOT_FOUND_EXCEPTION));
	}
}
