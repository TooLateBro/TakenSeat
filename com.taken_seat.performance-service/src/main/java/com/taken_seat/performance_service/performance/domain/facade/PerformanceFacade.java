package com.taken_seat.performance_service.performance.domain.facade;

import java.util.List;
import java.util.UUID;

import com.taken_seat.performance_service.performance.domain.model.Performance;
import com.taken_seat.performance_service.performance.presentation.dto.request.RecommendedPerformanceDto;

public interface PerformanceFacade {

	Performance getByPerformanceId(UUID performanceId);

	/**
	 * 주어진 공연 ID 목록으로부터,
	 * 외부에 노출할 DTO 형태의 공연 정보를 조회해서 반환합니다.
	 */
	List<RecommendedPerformanceDto> findRecommendedPerformancesByIds(List<UUID> ids);
}
