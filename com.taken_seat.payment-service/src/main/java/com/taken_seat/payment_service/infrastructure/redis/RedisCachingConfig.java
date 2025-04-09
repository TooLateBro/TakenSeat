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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
	public ObjectMapper redisObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.registerModule(new Jdk8Module()); // Jdk8Module 추가
		objectMapper.registerModule(new JavaTimeModule()); // JavaTimeModule 추가
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		return objectMapper;
	}

	@Bean
	public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
		ObjectMapper objectMapper = redisObjectMapper();

		Jackson2JsonRedisSerializer<PaymentDetailResDto> paymentDetailSerializer =
			new Jackson2JsonRedisSerializer<>(objectMapper, PaymentDetailResDto.class);

		Jackson2JsonRedisSerializer<PagePaymentResponseDto> pagePaymentSerializer =
			new Jackson2JsonRedisSerializer<>(objectMapper, PagePaymentResponseDto.class);

		RedisCacheConfiguration defaultConfiguration = RedisCacheConfiguration
			.defaultCacheConfig()
			.entryTtl(DEFAULT_TTL)
			.disableCachingNullValues()
			.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
			.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(paymentDetailSerializer));

		// PaymentCache 설정 (10분 TTL, PaymentDetailResDto 직렬화)
		RedisCacheConfiguration orderConfiguration = RedisCacheConfiguration
			.defaultCacheConfig()
			.entryTtl(PAYMENT_TTL)
			.disableCachingNullValues()
			.computePrefixWith(CacheKeyPrefix.simple())
			.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
			.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(paymentDetailSerializer));

		// PaymentSearchCache 설정 (1시간 TTL, PagePaymentResponseDto 직렬화)
		RedisCacheConfiguration orderSearchConfiguration = RedisCacheConfiguration
			.defaultCacheConfig()
			.entryTtl(PAYMENT_SEARCH_TTL)
			.disableCachingNullValues()
			.computePrefixWith(CacheKeyPrefix.simple())
			.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
			.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(pagePaymentSerializer));

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
