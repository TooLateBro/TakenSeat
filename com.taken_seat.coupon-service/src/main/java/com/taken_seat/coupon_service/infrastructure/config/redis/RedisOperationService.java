package com.taken_seat.coupon_service.infrastructure.config.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
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

    public RedisOperationService(RedisTemplate<String, Long> longRedisTemplate) {
        this.longRedisTemplate = longRedisTemplate;
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

    public Long evalScript(String luaScript, String redisKey, String issuedUserKey, String userId) {
        // Redis에 전달할 Lua 스크립트 객체 생성
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        // Lua 스크립트 본문 설정
        redisScript.setScriptText(luaScript);
        // 스크립트 실행 결과 타입을 Long으로 지정
        redisScript.setResultType(Long.class);
        // Redis에서 Lua 스크립트 실행
        // KEYS = redisKey (쿠폰 수량), issuedUserKey (발급된 유저 목록)
        // ARGV = userId (발급하려는 사용자 ID)
        return longRedisTemplate.execute(
                redisScript,
                Arrays.asList(redisKey, issuedUserKey),
                userId
        );
    }
}