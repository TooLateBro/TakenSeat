package com.taken_seat.booking_service.booking.infrastructure.messaging;

import java.util.UUID;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import com.taken_seat.booking_service.booking.application.service.BookingService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisExpireListener implements MessageListener {

	private final BookingService bookingService;

	@Override
	public void onMessage(Message message, byte[] pattern) {
		String expiredKey = message.toString();

		UUID bookingId = UUID.fromString(expiredKey.substring("expire:".length()));
		bookingService.expireBooking(bookingId);
	}
}