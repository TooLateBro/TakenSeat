package com.taken_seat.auth_service.presentation.controller.user;

import com.taken_seat.auth_service.application.service.user.KafkaProducerService;
import com.taken_seat.common_service.message.KafkaUserInfoMessage;
import com.taken_seat.common_service.message.UserBenefitMessage;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {

    private final KafkaProducerService kafkaProducerService;

    public KafkaConsumer(KafkaProducerService kafkaProducerService) {
        this.kafkaProducerService = kafkaProducerService;
    }

    private static final String RESPONSE_TOPIC = "benefit.usage.response";
    private static final String RESPONSE_CANCEL_TOPIC = "benefit.refund.response";
    private static final String RESPONSE_TOPIC_REPLY = "Issuance-of-coupons-reply";


    @KafkaListener(groupId = "couponFIFO", topics = RESPONSE_TOPIC_REPLY)
    public void consume(@Payload KafkaUserInfoMessage message) {
        kafkaProducerService.createUserCoupon(message);
    }

    @KafkaListener(groupId = "${kafka.consumer.group-id}", topics = "${kafka.topic.benefit-usage-request}")
    @SendTo(RESPONSE_TOPIC)
    public UserBenefitMessage paymentConsume(@Payload UserBenefitMessage message) {
        UserBenefitMessage userBenefitMessage = kafkaProducerService.benefitUsage(message);
        return userBenefitMessage;
    }

    @KafkaListener(topics = RESPONSE_CANCEL_TOPIC)
    public void benefitCancelConsume(@Payload UserBenefitMessage message) {
        kafkaProducerService.benefitCancel(message);
    }
}
