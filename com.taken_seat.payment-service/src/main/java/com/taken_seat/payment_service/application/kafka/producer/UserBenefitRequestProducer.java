package com.taken_seat.payment_service.application.kafka.producer;

import com.taken_seat.common_service.message.UserBenefitMessage;

public interface UserBenefitRequestProducer {

	void sendBenefitUsageRequest(UserBenefitMessage message);
}
