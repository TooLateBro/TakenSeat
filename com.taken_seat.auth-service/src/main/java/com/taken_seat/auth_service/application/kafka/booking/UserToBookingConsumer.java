package com.taken_seat.auth_service.application.kafka.booking;

import com.taken_seat.common_service.message.UserBenefitMessage;
import org.springframework.messaging.handler.annotation.Payload;

public interface UserToBookingConsumer {

    UserBenefitMessage benefitConsume(@Payload UserBenefitMessage message);

    void benefitPaymentConsume(@Payload UserBenefitMessage message);
}
