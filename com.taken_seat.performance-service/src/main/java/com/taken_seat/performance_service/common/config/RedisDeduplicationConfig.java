package com.taken_seat.performance_service.common.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class RedisDeduplicationConfig {

	private final RedisConnectionFactory redisConnectionFactory;

	private static final Duration SEAT_STATUS_TTL = Duration.ofSeconds(5);

	@Bean("deduplicationStringRedisTemplate")
	public StringRedisTemplate deduplicationStringRedisTemplate() {
		StringRedisTemplate stringRedisTemplate =
			new StringRedisTemplate(redisConnectionFactory);
		return stringRedisTemplate;
	}

	@Bean
	public Duration seatStatusTtl() {
		return SEAT_STATUS_TTL;
	}
}
