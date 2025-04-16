package com.taken_seat.review_service.infrastructure.config.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.script.RedisScript;

@Configuration
public class RedisLuaScriptConfig {

	@Bean
	public RedisScript<Boolean> saveAvgRatingScript() {
		Resource scriptSource = new ClassPathResource("luaScripts/saveAvgRating.lua");
		return RedisScript.of(scriptSource, Boolean.class);
	}

}
