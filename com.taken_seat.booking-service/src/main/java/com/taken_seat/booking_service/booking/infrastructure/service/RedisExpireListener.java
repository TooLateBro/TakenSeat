package com.taken_seat.booking_service.booking.infrastructure.service;

import java.util.UUID;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import com.taken_seat.booking_service.booking.application.service.BookingProducer;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisExpireListener implements MessageListener {

	private final BookingProducer bookingProducer;

	@Override
	public void onMessage(Message message, byte[] pattern) {
		String expiredKey = message.toString();

		if (!expiredKey.startsWith("expire:")) {
			return;
		}

		UUID bookingId = UUID.fromString(expiredKey.substring("expire:".length()));
		bookingProducer.sendBookingExpireEvent(bookingId);
	}
}