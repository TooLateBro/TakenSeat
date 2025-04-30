package com.taken_seat.booking_service.booking.infrastructure.messaging;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.taken_seat.booking_service.booking.application.dto.event.BookingEntityEvent;
import com.taken_seat.booking_service.booking.application.service.BookingProducer;
import com.taken_seat.booking_service.common.message.TicketRequestMessage;
import com.taken_seat.common_service.message.PaymentMessage;
import com.taken_seat.common_service.message.PaymentRefundMessage;
import com.taken_seat.common_service.message.QueueEnterMessage;
import com.taken_seat.common_service.message.UserBenefitMessage;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BookingKafkaProducer implements BookingProducer {

	private final KafkaTemplate<String, Object> kafkaTemplate;

	@Value("${kafka.topic.payment-request}")
	private String PAYMENT_REQUEST_TOPIC;

	@Value("${kafka.topic.ticket-request}")
	private String TICKET_REQUEST_TOPIC;

	@Value("${kafka.topic.benefit-usage-request}")
	private String BENEFIT_USAGE_REQUEST_TOPIC;

	@Value("${kafka.topic.benefit-refund-request}")
	private String BENEFIT_REFUND_REQUEST_TOPIC;

	@Value("${kafka.topic.payment-refund-request}")
	private String PAYMENT_REFUND_REQUEST_TOPIC;

	@Value("${kafka.topic.queue-response}")
	private String QUEUE_RESPONSE_TOPIC;

	@Value("${kafka.topic.booking-expire}")
	private String BOOKING_EXPIRE_TOPIC;

	@Value("${kafka.topic.booking-created}")
	private String BOOKING_CREATED_TOPIC;

	@Value("${kafka.topic.booking-updated}")
	private String BOOKING_UPDATED_TOPIC;

	@Override
	public void sendPaymentRequest(PaymentMessage message) {

		kafkaTemplate.send(PAYMENT_REQUEST_TOPIC, message);
	}

	@Override
	public void sendTicketRequest(TicketRequestMessage message) {

		kafkaTemplate.send(TICKET_REQUEST_TOPIC, message);
	}

	@Override
	public void sendBenefitUsageRequest(UserBenefitMessage message) {

		kafkaTemplate.send(BENEFIT_USAGE_REQUEST_TOPIC, message);
	}

	@Override
	public void sendBenefitRefundRequest(UserBenefitMessage message) {

		kafkaTemplate.send(BENEFIT_REFUND_REQUEST_TOPIC, message);
	}

	@Override
	public void sendPaymentRefundRequest(PaymentRefundMessage message) {

		kafkaTemplate.send(PAYMENT_REFUND_REQUEST_TOPIC, message);
	}

	@Override
	public void sendQueueEnterResponse(QueueEnterMessage message) {

		kafkaTemplate.send(QUEUE_RESPONSE_TOPIC, message);
	}

	@Override
	public void sendBookingExpireEvent(UUID bookingId) {

		kafkaTemplate.send(BOOKING_EXPIRE_TOPIC, bookingId);
	}

	@Override
	public void sendBookingCreatedEvent(BookingEntityEvent event) {

		kafkaTemplate.send(BOOKING_CREATED_TOPIC, event);
	}

	@Override
	public void sendBookingUpdatedEvent(BookingEntityEvent event) {

		kafkaTemplate.send(BOOKING_UPDATED_TOPIC, event);
	}
}