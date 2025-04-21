package com.taken_seat.performance_service.performance.domain.validator;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.taken_seat.common_service.exception.customException.PerformanceException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import com.taken_seat.performance_service.performance.domain.model.Performance;
import com.taken_seat.performance_service.performance.domain.repository.PerformanceRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PerformanceExistenceValidator {

	private final PerformanceRepository performanceRepository;

	public Performance validateByPerformanceId(UUID id) {
		return performanceRepository.findById(id)
			.orElseThrow(() -> new PerformanceException(ResponseCode.PERFORMANCE_NOT_FOUND_EXCEPTION));
	}

	public Performance validateByPerformanceScheduleId(UUID performanceScheduleId) {
		return performanceRepository.findByPerformanceScheduleId(performanceScheduleId)
			.orElseThrow(() -> new PerformanceException(ResponseCode.PERFORMANCE_NOT_FOUND_EXCEPTION));
	}
}
