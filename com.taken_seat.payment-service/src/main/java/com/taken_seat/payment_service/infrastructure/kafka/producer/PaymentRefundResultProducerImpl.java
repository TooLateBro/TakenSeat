package com.taken_seat.payment_service.infrastructure.kafka.producer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.taken_seat.common_service.message.PaymentRefundMessage;
import com.taken_seat.payment_service.application.kafka.producer.PaymentRefundResultProducer;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentRefundResultProducerImpl implements PaymentRefundResultProducer {

	private final KafkaTemplate<String, Object> kafkaTemplate;

	@Value("${kafka.topic.refund-response}")
	private String SEND_PAYMENT_REFUND_RESPONSE_TOPIC;

	@Override
	public void sendPaymentRefundResult(PaymentRefundMessage message) {
		kafkaTemplate.send(SEND_PAYMENT_REFUND_RESPONSE_TOPIC, message);
	}
}
