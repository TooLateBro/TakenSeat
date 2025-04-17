package com.taken_seat.booking_service.booking.application.service;

import java.time.Duration;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisService {

	private final RedisTemplate<String, String> redisTemplate;

	@Value("${variable.booking-expiration-time}")
	private long BOOKING_EXPIRATION_TIME;

	public void setBookingExpire(UUID bookingId) {
		redisTemplate.opsForValue().set("expire:" + bookingId, "", Duration.ofSeconds(BOOKING_EXPIRATION_TIME));
	}
}