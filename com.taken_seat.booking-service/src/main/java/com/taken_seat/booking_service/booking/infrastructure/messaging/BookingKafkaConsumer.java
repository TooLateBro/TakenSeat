package com.taken_seat.booking_service.booking.infrastructure.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.taken_seat.booking_service.booking.application.service.BookingService;
import com.taken_seat.booking_service.booking.presentation.BookingConsumer;
import com.taken_seat.common_service.message.PaymentMessage;
import com.taken_seat.common_service.message.PaymentRefundMessage;
import com.taken_seat.common_service.message.UserBenefitMessage;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BookingKafkaConsumer implements BookingConsumer {

	private final BookingService bookingService;

	@Override
	@KafkaListener(topics = "${kafka.topic.payment-result}", groupId = "${kafka.consumer.group-id.booking-service}")
	public void updateBooking(PaymentMessage message) {

		bookingService.updateBooking(message);
	}

	@KafkaListener(topics = "${kafka.topic.payment-refund-result}", groupId = "${kafka.consumer.group-id.booking-service}")
	public void updateBooking(PaymentRefundMessage message) {

		bookingService.updateBooking(message);
	}

	@Override
	@KafkaListener(topics = "${kafka.topic.benefit-usage-response}", groupId = "${kafka.consumer.group-id.booking-service}")
	public void createPayment(UserBenefitMessage message) {

		bookingService.createPayment(message);
	}

	@Override
	@KafkaListener(topics = "${kafka.topic.benefit-refund-response}", groupId = "${kafka.consumer.group-id.booking-service}")
	public void updateBenefitUsageHistory(UserBenefitMessage message) {

		bookingService.updateBenefitUsageHistory(message);
	}
}