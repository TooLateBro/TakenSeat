package com.taken_seat.performance_service.recommend.infrastructure.redis;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationCacheService {

	private static final String RECOMMENDATION_KEY_PREFIX = "recommendation:";
	public static final Duration SAVE_RECOMMENDATIONS_TTL = Duration.ofMinutes(20);
	public static final Duration PENDING_RECOMMENDATION_TTL = Duration.ofSeconds(30);
	public static final String PENDING_PLACEHOLDER = "PENDING";

	private final StringRedisTemplate redisTemplate;
	private final ObjectMapper objectMapper;

	public void saveRecommendations(UUID userId, List<UUID> performanceIds) {
		String key = cacheKey(userId);
		try {
			String value = objectMapper.writeValueAsString(performanceIds);
			redisTemplate.opsForValue().set(key, value, SAVE_RECOMMENDATIONS_TTL);
		} catch (JsonProcessingException e) {
			log.error("[Recommend] 추천 결과 캐싱 실패 - userId={}", userId, e);
		}
	}

	public boolean setPendingIfAbsent(UUID userId) {
		String key = cacheKey(userId);
		Boolean success = redisTemplate.opsForValue()
			.setIfAbsent(key, PENDING_PLACEHOLDER, PENDING_RECOMMENDATION_TTL);
		return Boolean.TRUE.equals(success);
	}

	public Optional<List<UUID>> getCachedRecommendations(UUID userId) {
		String key = cacheKey(userId);
		String value = redisTemplate.opsForValue().get(key);

		if (value == null || value.equals(PENDING_PLACEHOLDER)) {
			return Optional.empty();
		}

		try {
			List<UUID> result = objectMapper.readValue(
				value,
				new TypeReference<List<UUID>>() {
				}
			);
			return Optional.of(result);
		} catch (JsonProcessingException e) {
			log.error("[Recommend] 캐시 파싱 실패 - userId={}", userId, e);
			return Optional.empty();
		}
	}

	public void evict(UUID userId) {
		redisTemplate.delete(cacheKey(userId));
	}

	private String cacheKey(UUID userId) {
		return RECOMMENDATION_KEY_PREFIX + userId;
	}
}

