package com.taken_seat.payment_service.infrastructure.kafka.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.taken_seat.common_service.message.UserBenefitMessage;
import com.taken_seat.payment_service.application.kafka.consumer.UserBenefitResultListener;
import com.taken_seat.payment_service.application.service.UserBenefitEventHandlerService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserBenefitResultListenerImpl implements UserBenefitResultListener {

	private final UserBenefitEventHandlerService userBenefitEventHandlerService;

	@Override
	@KafkaListener(topics = "${kafka.topic.benefit-usage-response}", groupId = "${kafka.consumer.group-id}")
	public void handleBenefitUsageResult(UserBenefitMessage message) {
		userBenefitEventHandlerService.handleUserBenefitUsed(message);
	}
}
