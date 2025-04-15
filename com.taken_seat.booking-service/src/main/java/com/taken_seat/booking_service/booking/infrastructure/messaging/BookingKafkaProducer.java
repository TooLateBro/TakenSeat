package com.taken_seat.booking_service.booking.infrastructure.messaging;

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

	@Override
	public void sendPaymentRequestEvent(PaymentMessage message) {

		kafkaTemplate.send("payment.request", message);
	}

	@Override
	public void sendPaymentCompleteEvent(TicketRequestMessage message) {

		kafkaTemplate.send("ticket.request", message);
	}

	@Override
	public void sendBenefitUsageRequest(UserBenefitMessage message) {

		kafkaTemplate.send("benefit.usage.request", message);
	}

	@Override
	public void sendBenefitRefundRequest(UserBenefitMessage message) {

		kafkaTemplate.send("benefit.refund.request", message);
	}
}