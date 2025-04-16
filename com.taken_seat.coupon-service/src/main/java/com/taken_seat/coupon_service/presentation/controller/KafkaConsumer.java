package com.taken_seat.coupon_service.presentation.controller;

import com.taken_seat.common_service.message.KafkaUserInfoMessage;
import com.taken_seat.coupon_service.application.service.KafkaProducerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaConsumer {

    private final KafkaProducerService kafkaProducerService;

    public KafkaConsumer(KafkaProducerService kafkaProducerService) {
        this.kafkaProducerService = kafkaProducerService;
    }

    private static final String RESPONSE_TOPIC = "Issuance-of-coupons";
    private static final String RESPONSE_TOPIC_REPLY = "Issuance-of-coupons-reply";
    private static final String RECEIVE_MESSAGE_KEY = "Partitions-of-coupons";

    @KafkaListener(groupId = "couponFIFO", topics = RESPONSE_TOPIC)
    @SendTo(RESPONSE_TOPIC_REPLY)
    public KafkaUserInfoMessage consume(@Payload KafkaUserInfoMessage message,
                                        @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.info("Receive userId: {}", message.getUserId());
        try {
            if (key.equals(RECEIVE_MESSAGE_KEY)) {
                log.info("userId: {}", message.getUserId());
                return kafkaProducerService.producerMessage(message);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return message;
    }
}