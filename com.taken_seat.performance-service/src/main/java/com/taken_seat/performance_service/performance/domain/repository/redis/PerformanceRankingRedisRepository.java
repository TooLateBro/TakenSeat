package com.taken_seat.performance_service.performance.domain.repository.redis;

import java.util.List;
import java.util.UUID;

public interface PerformanceRankingRedisRepository {

	void incrementScore(UUID performanceId, double score);

	List<UUID> getTopPerformanceIds(int limit);
}
