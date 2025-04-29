package com.taken_seat.booking_service.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import com.taken_seat.booking_service.booking.infrastructure.service.RedisExpireListener;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class RedisListenerConfig {

	private final RedisExpireListener redisExpireListener;

	@Bean
	public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory) {
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);

		// 여기서 채널에 리스너 바인딩
		container.addMessageListener(redisExpireListener, new PatternTopic("__keyevent@0__:expired"));

		return container;
	}
}