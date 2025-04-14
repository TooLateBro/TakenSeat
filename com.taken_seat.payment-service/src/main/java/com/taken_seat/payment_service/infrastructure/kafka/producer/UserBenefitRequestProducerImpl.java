package com.taken_seat.payment_service.infrastructure.kafka.producer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.taken_seat.common_service.message.UserBenefitUsageRequestMessage;
import com.taken_seat.payment_service.application.kafka.producer.UserBenefitRequestProducer;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserBenefitRequestProducerImpl implements UserBenefitRequestProducer {

	private final KafkaTemplate<String, Object> kafkaTemplate;

	@Value("${kafka.topic.benefit-usage-request}")
	private String SEND_BENEFIT_USAGE_REQUEST_TOPIC;

	@Override
	public void sendBenefitUsageRequest(UserBenefitUsageRequestMessage message) {
		kafkaTemplate.send(SEND_BENEFIT_USAGE_REQUEST_TOPIC, message);
	}
}
