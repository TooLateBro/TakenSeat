package com.taken_seat.performance_service.performance.infrastructure.facade;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.taken_seat.performance_service.performance.domain.facade.PerformanceFacade;
import com.taken_seat.performance_service.performance.domain.model.Performance;
import com.taken_seat.performance_service.performance.domain.validator.PerformanceExistenceValidator;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PerformanceFacadeImpl implements PerformanceFacade {

	private final PerformanceExistenceValidator performanceExistenceValidator;

	@Override
	public Performance getByPerformanceId(UUID performanceId) {
		return performanceExistenceValidator.validateByPerformanceId(performanceId);
	}

	@Override
	public Performance getByPerformanceScheduleId(UUID performanceScheduleId) {
		return performanceExistenceValidator.validateByPerformanceScheduleId(performanceScheduleId);
	}
}
