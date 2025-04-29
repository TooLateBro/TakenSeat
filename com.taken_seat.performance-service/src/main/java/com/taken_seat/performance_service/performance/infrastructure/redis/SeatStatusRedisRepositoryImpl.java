package com.taken_seat.performance_service.performance.infrastructure.redis;

import java.util.Map;
import java.util.UUID;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import com.taken_seat.performance_service.performance.domain.repository.redis.SeatStatusRedisRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SeatStatusRedisRepositoryImpl implements SeatStatusRedisRepository {

	private final StringRedisTemplate redisTemplate;
	private static final String PREFIX = "seat:status:";
	private static final String DELETED_SUFFIX = ":deleted";

	private String getRedisKey(UUID performanceScheduleId) {
		return PREFIX + performanceScheduleId;
	}

	private boolean isDeleted(UUID performanceScheduleId) {
		return Boolean.TRUE.equals(
			redisTemplate.hasKey(
				getRedisKey(performanceScheduleId) + DELETED_SUFFIX));
	}

	@Override
	public void saveSeatStatus(UUID performanceScheduleId, UUID scheduleSeatId, String seatStatus) {

		if (isDeleted(performanceScheduleId))
			return;

		redisTemplate.opsForHash().put(
			getRedisKey(performanceScheduleId), scheduleSeatId.toString(), seatStatus);
	}

	@Override
	public String getSeatStatus(UUID performanceScheduleId, UUID scheduleSeatId) {
		if (isDeleted(performanceScheduleId))
			return null;
		Object status = redisTemplate.opsForHash().get(getRedisKey(performanceScheduleId), scheduleSeatId.toString());
		return status != null ? status.toString() : null;
	}

	@Override
	public Map<Object, Object> getAllSeatStatus(UUID performanceScheduleId) {
		if (isDeleted(performanceScheduleId))
			return Map.of();
		return redisTemplate.opsForHash().entries(getRedisKey(performanceScheduleId));
	}

	@Override
	public void deleteSeatStatus(UUID performanceScheduleId, UUID scheduleSeatId) {
		if (isDeleted(performanceScheduleId))
			return;
		redisTemplate.opsForHash().delete(getRedisKey(performanceScheduleId), scheduleSeatId.toString());
	}

	@Override
	public void deleteAllSeatStatus(UUID performanceScheduleId) {
		redisTemplate.opsForValue().set(getRedisKey(performanceScheduleId) + DELETED_SUFFIX, "true");
	}
}
