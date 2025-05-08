package com.taken_seat.auth_service.infrastructure.kafka.booking;

import com.taken_seat.auth_service.application.kafka.booking.UserToBookingConsumer;
import com.taken_seat.auth_service.application.kafka.booking.UserToBookingConsumerService;
import com.taken_seat.common_service.message.UserBenefitMessage;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Component
public class UserToBookingConsumerImpl implements UserToBookingConsumer {

    private final UserToBookingConsumerService userToBookingConsumerService;

    public UserToBookingConsumerImpl(UserToBookingConsumerService userToBookingConsumerService) {
        this.userToBookingConsumerService = userToBookingConsumerService;
    }

    @Override
    @KafkaListener(groupId = "${kafka.consumer.group-id}", topics = "${kafka.topic.benefit-usage-request}")
    @SendTo("benefit.usage.response") // @SendTo는 컴파일 시점에 값이 결정되어야 하는 정적 애노테이션이기 때문에 직접 입력
    public UserBenefitMessage benefitConsume(@Payload UserBenefitMessage message) {
        UserBenefitMessage userBenefitMessage = userToBookingConsumerService.benefitUsage(message);
        return userBenefitMessage;
    }

    @Override
    @KafkaListener(groupId = "${kafka.consumer.group-id}", topics = "${kafka.topic.benefit-refund-request}")
    public void benefitPaymentConsume(@Payload UserBenefitMessage message) {
        userToBookingConsumerService.benefitPayment(message);
    }
}
