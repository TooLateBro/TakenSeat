package com.taken_seat.payment_service.infrastructure.kafka.producer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.taken_seat.common_service.message.PaymentMessage;
import com.taken_seat.payment_service.application.kafka.producer.PaymentResponseProducer;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentResponseProducerImpl implements PaymentResponseProducer {

	private final KafkaTemplate<String, Object> kafkaTemplate;

	@Value("${kafka.topic.payment-response}")
	private String SEND_PAYMENT_RESPONSE_TOPIC;

	@Override
	public void sendPaymentResponse(PaymentMessage message) {
		kafkaTemplate.send(SEND_PAYMENT_RESPONSE_TOPIC, message);
	}
}
