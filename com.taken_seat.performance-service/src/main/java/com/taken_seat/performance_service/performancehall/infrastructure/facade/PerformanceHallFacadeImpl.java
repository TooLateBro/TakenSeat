package com.taken_seat.performance_service.performancehall.infrastructure.facade;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.taken_seat.performance_service.performancehall.application.dto.command.SeatTemplateInfo;
import com.taken_seat.performance_service.performancehall.domain.facade.PerformanceHallFacade;
import com.taken_seat.performance_service.performancehall.domain.model.PerformanceHall;
import com.taken_seat.performance_service.performancehall.domain.validation.PerformanceHallExistenceValidator;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PerformanceHallFacadeImpl implements PerformanceHallFacade {

	private final PerformanceHallExistenceValidator performanceHallExistenceValidator;

	@Override
	public PerformanceHall getByPerformanceHallId(UUID performanceHallId) {
		return performanceHallExistenceValidator.validateByPerformanceHallId(performanceHallId);
	}

	@Override
	public boolean isSoldOut(UUID performanceHallId) {
		PerformanceHall performanceHall = performanceHallExistenceValidator.validateByPerformanceHallId(
			performanceHallId);
		return performanceHall.isSoldOut();
	}

	@Override
	public List<SeatTemplateInfo> getSeatTemplate(UUID performanceHallId) {
		PerformanceHall performanceHall = performanceHallExistenceValidator.validateByPerformanceHallId(
			performanceHallId
		);
		return performanceHall.toSeatTemplateInfos();
	}
}
