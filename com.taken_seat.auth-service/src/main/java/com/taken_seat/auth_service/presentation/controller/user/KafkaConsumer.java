package com.taken_seat.auth_service.presentation.controller.user;

import com.taken_seat.common_service.message.KafkaUserInfoMessage;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {

    private static final String RESPONSE_TOPIC_REPLY = "Issuance-of-coupons-reply";

    @KafkaListener(groupId = "couponFIFO", topics = RESPONSE_TOPIC_REPLY)
    public KafkaUserInfoMessage consume(@Payload KafkaUserInfoMessage message) {
        System.out.printf("message: %s\n", message);
        return message;
    }
}
