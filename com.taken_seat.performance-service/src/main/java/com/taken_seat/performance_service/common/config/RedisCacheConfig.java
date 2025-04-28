package com.taken_seat.performance_service.common.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taken_seat.common_service.dto.response.PerformanceEndTimeDto;
import com.taken_seat.common_service.dto.response.PerformanceStartTimeDto;
import com.taken_seat.performance_service.performance.presentation.dto.response.DetailResponseDto;
import com.taken_seat.performance_service.performance.presentation.dto.response.PageResponseDto;
import com.taken_seat.performance_service.performancehall.presentation.dto.response.HallDetailResponseDto;
import com.taken_seat.performance_service.performancehall.presentation.dto.response.HallPageResponseDto;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableCaching
@RequiredArgsConstructor
public class RedisCacheConfig {

	private final RedisConnectionFactory redisConnectionFactory;
	private final ObjectMapper objectMapper;

	public static final String PERFORMANCE_DETAIL = "performance:detail";
	public static final String PERFORMANCE_SEARCH = "performance:search";
	public static final String PERFORMANCE_HALL_DETAIL = "performanceHall:detail";
	public static final String PERFORMANCE_HALL_SEARCH = "performanceHall:search";
	public static final String SCHEDULE_START_TIME = "performanceSchedule:startTime";
	public static final String SCHEDULE_END_TIME = "performanceSchedule:endTime";

	public static final Duration PERFORMANCE_DETAIL_TTL = Duration.ofMinutes(10);
	public static final Duration PERFORMANCE_SEARCH_TTL = Duration.ofMinutes(5);
	public static final Duration PERFORMANCE_HALL_DETAIL_TTL = Duration.ofMinutes(10);
	public static final Duration PERFORMANCE_HALL_SEARCH_TTL = Duration.ofMinutes(5);
	public static final Duration SCHEDULE_TIME_TTL = Duration.ofMinutes(15);

	@Bean
	public RedisCacheManager cacheManager() {

		RedisSerializationContext.SerializationPair<String> keyPair =
			RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer());

		Jackson2JsonRedisSerializer<DetailResponseDto> performanceDetailSerializer =
			new Jackson2JsonRedisSerializer<>(objectMapper, DetailResponseDto.class);

		Jackson2JsonRedisSerializer<PageResponseDto> performancePageSerializer =
			new Jackson2JsonRedisSerializer<>(objectMapper, PageResponseDto.class);

		Jackson2JsonRedisSerializer<HallDetailResponseDto> hallDetailSerializer =
			new Jackson2JsonRedisSerializer<>(objectMapper, HallDetailResponseDto.class);

		Jackson2JsonRedisSerializer<HallPageResponseDto> hallPageSerializer =
			new Jackson2JsonRedisSerializer<>(objectMapper, HallPageResponseDto.class);

		Jackson2JsonRedisSerializer<PerformanceStartTimeDto> startSerializer =
			new Jackson2JsonRedisSerializer<>(objectMapper, PerformanceStartTimeDto.class);

		Jackson2JsonRedisSerializer<PerformanceEndTimeDto> endSerSerializer =
			new Jackson2JsonRedisSerializer<>(objectMapper, PerformanceEndTimeDto.class);

		RedisSerializationContext.SerializationPair<Object> genericValuePair =
			RedisSerializationContext.SerializationPair
				.fromSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));

		RedisCacheConfiguration defaultConfig =
			RedisCacheConfiguration.defaultCacheConfig()
				.disableCachingNullValues()
				.serializeKeysWith(keyPair)
				.serializeValuesWith(genericValuePair);

		Map<String, RedisCacheConfiguration> configs = new HashMap<>();

		configs.put(
			PERFORMANCE_DETAIL,
			defaultConfig
				.entryTtl(PERFORMANCE_DETAIL_TTL)
				.serializeValuesWith(
					RedisSerializationContext
						.SerializationPair
						.fromSerializer(performanceDetailSerializer))
		);

		configs.put(
			PERFORMANCE_SEARCH,
			defaultConfig
				.entryTtl(PERFORMANCE_SEARCH_TTL)
				.serializeValuesWith(
					RedisSerializationContext
						.SerializationPair
						.fromSerializer(performancePageSerializer)
				));

		configs.put(
			PERFORMANCE_HALL_DETAIL,
			defaultConfig
				.entryTtl(PERFORMANCE_HALL_DETAIL_TTL)
				.serializeValuesWith(
					RedisSerializationContext
						.SerializationPair
						.fromSerializer(hallDetailSerializer)
				));

		configs.put(
			PERFORMANCE_HALL_SEARCH,
			defaultConfig
				.entryTtl(PERFORMANCE_HALL_SEARCH_TTL)
				.serializeValuesWith(
					RedisSerializationContext
						.SerializationPair
						.fromSerializer(hallPageSerializer)
				));

		configs.put(
			SCHEDULE_START_TIME,
			defaultConfig
				.entryTtl(SCHEDULE_TIME_TTL)
				.serializeValuesWith(
					RedisSerializationContext
						.SerializationPair
						.fromSerializer(startSerializer)
				));

		configs.put(
			SCHEDULE_END_TIME,
			defaultConfig
				.entryTtl(SCHEDULE_TIME_TTL)
				.serializeValuesWith(
					RedisSerializationContext
						.SerializationPair
						.fromSerializer(endSerSerializer)
				));

		return RedisCacheManager.builder(redisConnectionFactory)
			.cacheDefaults(defaultConfig)
			.withInitialCacheConfigurations(configs)
			.build();
	}
}
