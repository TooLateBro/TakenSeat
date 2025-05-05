package com.taken_seat.coupon_service.infrastructure.config.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Arrays;

/**
 * Redis 작업을 위한 서비스 클래스
 * 단위 테스트를 용이하게 하기 위해 Redis 관련 로직을 분리
 */
@Service
public class RedisOperationService {

    private final RedisTemplate<String, Long> longRedisTemplate;
    private final RedisScript<Long> redisLuaScript;

    public RedisOperationService(RedisTemplate<String, Long> longRedisTemplate,
                                 RedisScript<Long> redisLuaScript) {
        this.longRedisTemplate = longRedisTemplate;
        this.redisLuaScript = redisLuaScript;
    }

    public boolean hasKey(String key) {
        Boolean exists = longRedisTemplate.hasKey(key);
        return exists != null && exists;
    }

    public void initializeQuantity(String key, Long quantity) {
        longRedisTemplate.opsForValue().set(key, quantity, Duration.ofMinutes(1));
    }

    public Long getCurrentQuantity(String key) {
        return longRedisTemplate.opsForValue().get(key);
    }

    public Long evalScript(String redisKey, String issuedUserKey, String userId) {
        return longRedisTemplate.execute(
                redisLuaScript,
                Arrays.asList(redisKey, issuedUserKey),
                userId
        );
    }
}
