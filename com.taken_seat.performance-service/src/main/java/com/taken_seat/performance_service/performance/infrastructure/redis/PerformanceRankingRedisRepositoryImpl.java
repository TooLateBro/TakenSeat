package com.taken_seat.performance_service.performance.infrastructure.redis;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import com.taken_seat.performance_service.performance.domain.repository.redis.PerformanceRankingRedisRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PerformanceRankingRedisRepositoryImpl implements PerformanceRankingRedisRepository {

	private final StringRedisTemplate redisTemplate;
	private static final String KEY = "ranking:weekly";

	@Override
	public void incrementScore(UUID performanceId, double score) {

		redisTemplate.opsForZSet().incrementScore(KEY, performanceId.toString(), score);
	}

	@Override
	public List<UUID> getTopPerformanceIds(int limit) {
		Set<String> performanceIdStrings =
			redisTemplate.opsForZSet().reverseRange(KEY, 0, limit - 1);

		if (performanceIdStrings == null || performanceIdStrings.isEmpty()) {
			return List.of();
		}

		return performanceIdStrings.stream()
			.map(UUID::fromString)
			.toList();
	}
}
