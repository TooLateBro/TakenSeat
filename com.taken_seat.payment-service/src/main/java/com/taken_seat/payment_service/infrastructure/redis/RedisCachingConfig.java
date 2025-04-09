package com.taken_seat.payment_service.infrastructure.redis;

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

import com.taken_seat.payment_service.application.dto.response.PagePaymentResponseDto;
import com.taken_seat.payment_service.application.dto.response.PaymentDetailResDto;

@Configuration
@EnableCaching
public class RedisCachingConfig {

	public static final String PAYMENT_CACHE = "paymentCache";
	public static final String PAYMENT_SEARCH_CACHE = "paymentSearchCache";

	private static final Duration DEFAULT_TTL = Duration.ofSeconds(120);
	private static final Duration PAYMENT_TTL = Duration.ofMinutes(10);
	private static final Duration PAYMENT_SEARCH_TTL = Duration.ofHours(1);

	@Bean
	public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {

		RedisCacheConfiguration defaultConfiguration = RedisCacheConfiguration
			.defaultCacheConfig()
			.entryTtl(DEFAULT_TTL)
			.disableCachingNullValues()
			.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
			.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
				new Jackson2JsonRedisSerializer<>(PaymentDetailResDto.class)));

		// PaymentCache 설정 (10분 TTL, PaymentDetailResDto 직렬화)
		RedisCacheConfiguration orderConfiguration = RedisCacheConfiguration
			.defaultCacheConfig()
			.entryTtl(PAYMENT_TTL)
			.disableCachingNullValues()
			.computePrefixWith(CacheKeyPrefix.simple())
			.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
			.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
				new Jackson2JsonRedisSerializer<>(PaymentDetailResDto.class)));

		// OrderSearchCache 설정 (1시간 TTL, OrderSearchResDto 직렬화)
		RedisCacheConfiguration orderSearchConfiguration = RedisCacheConfiguration
			.defaultCacheConfig()
			.entryTtl(PAYMENT_SEARCH_TTL)
			.disableCachingNullValues()
			.computePrefixWith(CacheKeyPrefix.simple())
			.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
			.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
				new Jackson2JsonRedisSerializer<>(PagePaymentResponseDto.class)));

		// 각 캐시 별로 설정을 관리하는 Map을 생성
		Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
		cacheConfigurations.put(PAYMENT_CACHE, orderConfiguration);
		cacheConfigurations.put(PAYMENT_SEARCH_CACHE, orderSearchConfiguration);

		return RedisCacheManager
			.builder(redisConnectionFactory)
			.cacheDefaults(defaultConfiguration)
			.withInitialCacheConfigurations(cacheConfigurations)
			.build();
	}

}
