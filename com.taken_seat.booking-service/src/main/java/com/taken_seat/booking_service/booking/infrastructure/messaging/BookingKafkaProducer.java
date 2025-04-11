package com.taken_seat.booking_service.booking.infrastructure.messaging;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.taken_seat.booking_service.booking.application.service.BookingEventProducer;
import com.taken_seat.booking_service.booking.domain.Booking;
import com.taken_seat.common_service.dto.event.BookingCreatedEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BookingKafkaProducer implements BookingEventProducer {

	private final KafkaTemplate<String, Object> kafkaTemplate;

	@Override
	public void sendBookingCreatedEvent(Booking booking, int price) {
		BookingCreatedEvent event = new BookingCreatedEvent(
			booking.getId(),
			booking.getUserId(),
			booking.getPerformanceId(),
			booking.getPerformanceScheduleId(),
			price
		);

		kafkaTemplate.send("booking-created", booking.getId().toString(), event);
	}
}