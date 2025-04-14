package com.taken_seat.booking_service.booking.infrastructure.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.taken_seat.booking_service.booking.application.service.BookingService;
import com.taken_seat.booking_service.booking.presentation.BookingConsumer;
import com.taken_seat.common_service.message.PaymentResultMessage;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BookingKafkaConsumer implements BookingConsumer {

	private final BookingService bookingService;

	@Override
	@KafkaListener(topics = "payment.result", groupId = "booking-service")
	public void updateBooking(PaymentResultMessage message) {

		bookingService.updateBooking(message);
	}
}