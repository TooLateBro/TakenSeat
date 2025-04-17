package com.taken_seat.booking_service.booking.infrastructure.messaging;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.taken_seat.booking_service.booking.application.service.BookingProducer;
import com.taken_seat.booking_service.common.message.TicketRequestMessage;
import com.taken_seat.common_service.message.PaymentMessage;
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

	@Value("${kafka.topic.benefit-payment-result}")
	private String BENEFIT_PAYMENT_RESULT_TOPIC;

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
	public void sendBenefitPaymentResult(UserBenefitMessage message) {

		kafkaTemplate.send(BENEFIT_PAYMENT_RESULT_TOPIC, message);
	}
}