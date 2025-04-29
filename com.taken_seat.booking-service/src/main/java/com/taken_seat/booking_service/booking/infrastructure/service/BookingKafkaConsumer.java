package com.taken_seat.booking_service.booking.infrastructure.service;

import java.util.UUID;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.taken_seat.booking_service.booking.application.dto.event.BookingEntityEvent;
import com.taken_seat.booking_service.booking.application.service.BookingService;
import com.taken_seat.common_service.message.BookingRequestMessage;
import com.taken_seat.common_service.message.PaymentMessage;
import com.taken_seat.common_service.message.PaymentRefundMessage;
import com.taken_seat.common_service.message.UserBenefitMessage;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BookingKafkaConsumer {

	private final BookingService bookingService;

	@KafkaListener(topics = "${kafka.topic.payment-response}", groupId = "${kafka.consumer.group-id.booking-command}")
	public void receivePaymentMessage(PaymentMessage message) {

		bookingService.receivePaymentMessage(message);
	}

	@KafkaListener(topics = "${kafka.topic.payment-refund-response}", groupId = "${kafka.consumer.group-id.booking-command}")
	public void receivePaymentRefundMessage(PaymentRefundMessage message) {

		bookingService.receivePaymentRefundMessage(message);
	}

	@KafkaListener(topics = "${kafka.topic.benefit-usage-response}", groupId = "${kafka.consumer.group-id.booking-command}")
	public void receiveBenefitUsageMessage(UserBenefitMessage message) {

		bookingService.receiveBenefitUsageMessage(message);
	}

	@KafkaListener(topics = "${kafka.topic.benefit-refund-response}", groupId = "${kafka.consumer.group-id.booking-command}")
	public void receiveBenefitRefundMessage(UserBenefitMessage message) {

		bookingService.receiveBenefitRefundMessage(message);
	}

	@KafkaListener(topics = "${kafka.topic.queue-request}", groupId = "${kafka.consumer.group-id.booking-command}")
	public void receiveWaitingQueueMessage(BookingRequestMessage message) {

		bookingService.receiveWaitingQueueMessage(message);
	}

	@KafkaListener(topics = "${kafka.topic.booking-expire}", groupId = "${kafka.consumer.group-id.booking-command}")
	public void receiveBookingExpireEvent(UUID bookingId) {

		bookingService.receiveBookingExpireEvent(bookingId);
	}

	@KafkaListener(topics = "${kafka.topic.booking-created}", groupId = "${kafka.consumer.group-id.booking-query}")
	public void receiveBookingCreatedEvent(BookingEntityEvent event) {

		bookingService.receiveBookingCreatedEvent(event);
	}

	@KafkaListener(topics = "${kafka.topic.booking-updated}", groupId = "${kafka.consumer.group-id.booking-query}")
	public void receiveBookingUpdatedEvent(BookingEntityEvent event) {

		bookingService.receiveBookingUpdatedEvent(event);
	}
}