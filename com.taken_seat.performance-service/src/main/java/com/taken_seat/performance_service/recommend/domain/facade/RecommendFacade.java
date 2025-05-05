package com.taken_seat.performance_service.recommend.domain.facade;

import java.util.List;
import java.util.UUID;

import com.taken_seat.performance_service.recommend.presentation.dto.response.RecommendedPerformanceResponseDto;

public interface RecommendFacade {

	/**
	 * 사용자에게 추천된 공연 목록을 반환합니다.
	 *
	 * @param userId 사용자 ID
	 * @return 추천 공연 목록
	 */
	List<RecommendedPerformanceResponseDto> getRecommendedPerformances(UUID userId);
}
