package com.taken_seat.booking_service.common.service;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisService {

	private final RedisTemplate<String, Object> redisTemplate;

	@Value("${variable.booking-expiration-time}")
	private long BOOKING_EXPIRATION_TIME;

	public void setBookingExpire(UUID bookingId) {
		redisTemplate.opsForValue().set("expire:" + bookingId, "", Duration.ofSeconds(BOOKING_EXPIRATION_TIME));
	}

	public void evictAllCaches(String cacheValue, String key) {
		String prefix = cacheValue + "::" + key;
		Set<String> keys = redisTemplate.keys(prefix + "*");

		if (keys != null && !keys.isEmpty()) {
			redisTemplate.delete(keys);
		}
	}

	public void removeBookingExpire(UUID bookingId) {
		redisTemplate.delete("expire:" + bookingId);
	}

	// public void evictCache(String cacheValue, String key) {
	// 	Cache cache = cacheManager.getCache(cacheValue);
	// 	if (cache != null) {
	// 		cache.evict(key);
	// 	}
	// }
}