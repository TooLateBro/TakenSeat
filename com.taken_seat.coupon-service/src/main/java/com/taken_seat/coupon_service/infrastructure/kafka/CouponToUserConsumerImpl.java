package com.taken_seat.coupon_service.infrastructure.kafka;

import com.taken_seat.common_service.message.KafkaUserInfoMessage;
import com.taken_seat.coupon_service.application.kafka.CouponToUserConsumer;
import com.taken_seat.coupon_service.application.kafka.CouponToUserConsumerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CouponToUserConsumerImpl implements CouponToUserConsumer {

    private final CouponToUserConsumerService couponToUserConsumerService;

    public CouponToUserConsumerImpl(CouponToUserConsumerService couponToUserConsumerService) {
        this.couponToUserConsumerService = couponToUserConsumerService;
    }

    private static final String RESPONSE_TOPIC = "Issuance-of-coupons";
    private static final String RESPONSE_TOPIC_REPLY = "Issuance-of-coupons-reply";
    private static final String RECEIVE_MESSAGE_KEY = "Partitions-of-coupons";

    @KafkaListener(groupId = "couponFIFO", topics = RESPONSE_TOPIC)
    @Override
    @SendTo(RESPONSE_TOPIC_REPLY)
    public KafkaUserInfoMessage consume(@Payload KafkaUserInfoMessage message,
                                        @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.info("Receive userId: {}", message.getUserId());
        try {
            if (key.equals(RECEIVE_MESSAGE_KEY)) {
                log.info("userId: {}", message.getUserId());
                return couponToUserConsumerService.producerMessage(message);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return message;
    }
}