package com.taken_seat.auth_service.application.kafka.booking;

import com.taken_seat.common_service.message.UserBenefitMessage;

public interface UserToBookingConsumerService {

    UserBenefitMessage benefitUsage(UserBenefitMessage message);

    void benefitPayment(UserBenefitMessage message);
}
