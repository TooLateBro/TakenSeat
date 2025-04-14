package com.taken_seat.payment_service.application.service;

import com.taken_seat.common_service.message.UserBenefitMessage;

public interface UserBenefitEventHandlerService {
	void handleUserBenefitUsed(UserBenefitMessage message);
}
