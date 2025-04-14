package com.taken_seat.payment_service.infrastructure.kafka.producer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.taken_seat.common_service.message.PaymentResultMessage;
import com.taken_seat.payment_service.application.kafka.producer.PaymentResultProducer;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentResultProducerImpl implements PaymentResultProducer {

	private final KafkaTemplate<String, Object> kafkaTemplate;

	@Value("${kafka.topic.payment-result}")
	private String SEND_PAYMENT_RESULT_TOPIC;

	@Override
	public void sendPaymentResult(PaymentResultMessage message) {
		kafkaTemplate.send(SEND_PAYMENT_RESULT_TOPIC, message.getPaymentId().toString(), message);
	}
}
