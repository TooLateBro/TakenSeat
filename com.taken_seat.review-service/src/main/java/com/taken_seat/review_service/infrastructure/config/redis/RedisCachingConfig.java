package com.taken_seat.review_service.infrastructure.config.redis;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.taken_seat.review_service.application.dto.response.PageReviewResponseDto;
import com.taken_seat.review_service.application.dto.response.ReviewDetailResDto;

@Configuration
@EnableCaching
public class RedisCachingConfig {

	public static final String REVIEW_CACHE = "reviewCache";
	public static final String REVIEW_SEARCH_CACHE = "reviewSearchCache";

	private static final Duration DEFAULT_TTL = Duration.ofSeconds(120);
	private static final Duration REVIEW_TTL = Duration.ofMinutes(10);
	private static final Duration REVIEW_SEARCH_TTL = Duration.ofHours(1);

	@Bean
	public ObjectMapper redisObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new Jdk8Module()); // Jdk8Module 추가
		objectMapper.registerModule(new JavaTimeModule()); // JavaTimeModule 추가
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		return objectMapper;
	}

	@Bean
	public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
		ObjectMapper objectMapper = redisObjectMapper();

		Jackson2JsonRedisSerializer<ReviewDetailResDto> reviewDetailSerializer =
			new Jackson2JsonRedisSerializer<>(objectMapper, ReviewDetailResDto.class);

		Jackson2JsonRedisSerializer<PageReviewResponseDto> pageReviewSerializer =
			new Jackson2JsonRedisSerializer<>(objectMapper, PageReviewResponseDto.class);

		RedisCacheConfiguration defaultConfiguration = RedisCacheConfiguration
			.defaultCacheConfig()
			.entryTtl(DEFAULT_TTL)
			.disableCachingNullValues()
			.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
			.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(reviewDetailSerializer));

		// ReviewCache 설정 (10분 TTL, ReviewDetailResDto 직렬화)
		RedisCacheConfiguration reviewConfiguration = RedisCacheConfiguration
			.defaultCacheConfig()
			.entryTtl(REVIEW_TTL)
			.disableCachingNullValues()
			.computePrefixWith(CacheKeyPrefix.simple())
			.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
			.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(reviewDetailSerializer));

		// ReviewSearchCache 설정 (1시간 TTL, PageReviewResponseDto 직렬화)
		RedisCacheConfiguration reviewSearchConfiguration = RedisCacheConfiguration
			.defaultCacheConfig()
			.entryTtl(REVIEW_SEARCH_TTL)
			.disableCachingNullValues()
			.computePrefixWith(CacheKeyPrefix.simple())
			.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
			.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(pageReviewSerializer));

		// 각 캐시 별로 설정을 관리하는 Map을 생성
		Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
		cacheConfigurations.put(REVIEW_CACHE, reviewConfiguration);
		cacheConfigurations.put(REVIEW_SEARCH_CACHE, reviewSearchConfiguration);

		return RedisCacheManager
			.builder(redisConnectionFactory)
			.cacheDefaults(defaultConfiguration)
			.withInitialCacheConfigurations(cacheConfigurations)
			.build();
	}
}
