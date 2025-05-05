package com.taken_seat.performance_service.recommend.infrastructure.redis;

import java.util.UUID;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecommendationCacheService {

	private final StringRedisTemplate redisTemplate;

	public void evict(UUID userId) {
		String key = cacheKey(userId);
		redisTemplate.delete(key);
	}

	private String cacheKey(UUID userId) {
		return "recommendation:" + userId.toString();
	}
}
