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

    private static final String RESPONSE_TOPIC = "benefit.usage.response";
    private static final String REQUEST_CANCEL_TOPIC = "benefit.refund.request";
    private static final String RESPONSE_CANCEL_TOPIC = "benefit.refund.response";

    @Override
    @KafkaListener(groupId = "${kafka.consumer.group-id}", topics = "${kafka.topic.benefit-usage-request}")
    @SendTo(RESPONSE_TOPIC)
    public UserBenefitMessage benefitConsume(@Payload UserBenefitMessage message) {
        UserBenefitMessage userBenefitMessage = userToBookingConsumerService.benefitUsage(message);
        return userBenefitMessage;
    }

    @Override
    @KafkaListener(groupId = "${kafka.consumer.group-id}", topics = REQUEST_CANCEL_TOPIC)
    @SendTo(RESPONSE_CANCEL_TOPIC)
    public UserBenefitMessage benefitCancelConsume(@Payload UserBenefitMessage message) {
        UserBenefitMessage userBenefitMessage = userToBookingConsumerService.benefitCancel(message);
        return userBenefitMessage;
    }
}
