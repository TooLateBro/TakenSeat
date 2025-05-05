package com.taken_seat.performance_service.performance.infrastructure.facade;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.taken_seat.performance_service.performance.domain.facade.PerformanceFacade;
import com.taken_seat.performance_service.performance.domain.model.Performance;
import com.taken_seat.performance_service.performance.domain.repository.PerformanceRepository;
import com.taken_seat.performance_service.performance.domain.validator.PerformanceExistenceValidator;
import com.taken_seat.performance_service.performance.presentation.dto.request.RecommendedPerformanceDto;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PerformanceFacadeImpl implements PerformanceFacade {

	private final PerformanceExistenceValidator performanceExistenceValidator;
	private final PerformanceRepository performanceRepository;

	@Override
	public Performance getByPerformanceId(UUID performanceId) {
		return performanceExistenceValidator.validateByPerformanceId(performanceId);
	}

	@Override
	public List<RecommendedPerformanceDto> findRecommendedPerformancesByIds(List<UUID> ids) {
		return performanceRepository.findAllById(ids).stream()
			.map(entity -> new RecommendedPerformanceDto(
				entity.getId(),
				entity.getTitle(),
				entity.getStartAt()
			))
			.collect(Collectors.toList());
	}
}
