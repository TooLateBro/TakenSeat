package com.taken_seat.coupon_service.infrastructure.config.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.script.RedisScript;

@Configuration
public class RedisLuaScriptConfig {

    @Bean
    public RedisScript<Long> redisLuaScript() {
        Resource resource = new ClassPathResource("lua/couponIssue.lua");
        return RedisScript.of(resource, Long.class);
    }
}
