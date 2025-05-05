package com.taken_seat.performance_service.recommend.infrastructure.facade;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.taken_seat.performance_service.recommend.application.service.RecommendQueryService;
import com.taken_seat.performance_service.recommend.domain.facade.RecommendFacade;
import com.taken_seat.performance_service.recommend.presentation.dto.response.RecommendedPerformanceResponseDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class RecommendFacadeImpl implements RecommendFacade {

	private final RecommendQueryService recommendQueryService;

	@Override
	public List<RecommendedPerformanceResponseDto> getRecommendedPerformances(UUID userId) {
		return recommendQueryService.recommendFor(userId);
	}
}
