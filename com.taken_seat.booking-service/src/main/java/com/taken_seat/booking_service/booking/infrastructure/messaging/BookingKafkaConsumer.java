package com.taken_seat.booking_service.booking.infrastructure.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.taken_seat.booking_service.booking.application.service.BookingConsumerService;
import com.taken_seat.booking_service.booking.presentation.BookingConsumer;
import com.taken_seat.common_service.message.BookingRequestMessage;
import com.taken_seat.common_service.message.PaymentMessage;
import com.taken_seat.common_service.message.PaymentRefundMessage;
import com.taken_seat.common_service.message.UserBenefitMessage;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BookingKafkaConsumer implements BookingConsumer {

	private final BookingConsumerService bookingConsumerService;

	@Override
	@KafkaListener(topics = "${kafka.topic.payment-response}", groupId = "${kafka.consumer.group-id.booking-service}")
	public void updateBooking(PaymentMessage message) {

		bookingConsumerService.updateBooking(message);
	}

	@KafkaListener(topics = "${kafka.topic.payment-refund-response}", groupId = "${kafka.consumer.group-id.booking-service}")
	public void updateBooking(PaymentRefundMessage message) {

		bookingConsumerService.updateBooking(message);
	}

	@Override
	@KafkaListener(topics = "${kafka.topic.benefit-usage-response}", groupId = "${kafka.consumer.group-id.booking-service}")
	public void createPayment(UserBenefitMessage message) {

		bookingConsumerService.createPayment(message);
	}

	@Override
	@KafkaListener(topics = "${kafka.topic.benefit-refund-response}", groupId = "${kafka.consumer.group-id.booking-service}")
	public void updateBenefitUsageHistory(UserBenefitMessage message) {

		bookingConsumerService.updateBenefitUsageHistory(message);
	}

	@Override
	@KafkaListener(topics = "${kafka.topic.queue-request}", groupId = "${kafka.consumer.group-id.booking-service}")
	public void acceptFromQueue(BookingRequestMessage message) {

		bookingConsumerService.acceptFromQueue(message);
	}
}