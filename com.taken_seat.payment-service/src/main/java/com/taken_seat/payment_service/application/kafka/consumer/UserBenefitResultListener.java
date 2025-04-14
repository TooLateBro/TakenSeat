package com.taken_seat.payment_service.application.kafka.consumer;

import com.taken_seat.common_service.message.UserBenefitMessage;

public interface UserBenefitResultListener {

	void handleBenefitUsageResult(UserBenefitMessage message);
}
