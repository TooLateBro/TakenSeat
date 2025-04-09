package com.taken_seat.auth_service.infrastructure.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;

@Configuration
@Slf4j
@EnableCaching // 캐싱을 사용하기 위한 설정
public class RedisCacheConfig {
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory){
        // CacheManager 가 사용할 설정을 구성
        // redis 를 이용해서 Spring Cache 를 사용할 때 Redis 관련 설정을 모아두는 클래스
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration
                .defaultCacheConfig()
                .disableCachingNullValues() // null 을 캐싱하지 않겠다.
                .entryTtl(Duration.ofSeconds(10)) // 캐시를 얼마나 유지할 것인가. 10초
                .serializeValuesWith( // 캐시에 저장할 value 를 어떻게 역.직렬화 할 것 인지 설정
                        // pageable 이 json serializer 와 잘 맞지 않아서 java 로 함
                        RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.json()));

        RedisCacheConfiguration getUserDetails = RedisCacheConfiguration
                .defaultCacheConfig()
                .disableCachingNullValues()
                .entryTtl(Duration.ofSeconds(10))
                .computePrefixWith(cacheName -> "userDetails : " + cacheName + "::")
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.json())
                );

        RedisCacheConfiguration searchUser = RedisCacheConfiguration
                .defaultCacheConfig()
                .disableCachingNullValues()
                .entryTtl(Duration.ofSeconds(10))
                .computePrefixWith(cacheName -> "searchUser : " + cacheName + "::")
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.json())
                );

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultConfig)
                .withCacheConfiguration("userDetails", getUserDetails)
                .withCacheConfiguration("searchUser", searchUser)
                .build();
    }

}