package com.taken_seat.review_service.infrastructure.redis.service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.taken_seat.common_service.exception.customException.ReviewException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import com.taken_seat.review_service.application.service.ReviewChangeMaker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Component
public class ReviewChangeMakerImpl implements ReviewChangeMaker {

	private final String PERFORMANCE_ID_SET_KEY = "review:changed:performanceId:set";
	private final RedisTemplate<String, String> redisTemplate;

	@Override
	public void markPerformanceChanged(UUID performanceId) {
		try {
			redisTemplate.opsForSet().add(PERFORMANCE_ID_SET_KEY, performanceId.toString());
		} catch (Exception e) {
			throw new ReviewException(ResponseCode.ILLEGAL_ARGUMENT);
		}
	}

	@Override
	public List<UUID> getChangedPerformanceIds() {
		Set<String> changedIds = redisTemplate.opsForSet().members(PERFORMANCE_ID_SET_KEY);
		if (changedIds == null || changedIds.isEmpty()) {
			log.info("[Review] 변경된 공연 없음. Redis 업데이트 생략");
			return List.of(); // 빈 리스트 반환
		}

		return changedIds.stream()
			.map(UUID::fromString)
			.collect(Collectors.toList());
	}

	@Override
	public void clearChangedPerformanceIds() {
		redisTemplate.delete(PERFORMANCE_ID_SET_KEY);
	}
}
