package com.taken_seat.auth_service.infrastructure.kafka.coupon;

import com.taken_seat.auth_service.application.kafka.coupon.UserToCouponConsumerService;
import com.taken_seat.auth_service.application.kafka.coupon.UserToCouponConsumer;
import com.taken_seat.common_service.message.KafkaUserInfoMessage;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class UserToCouponConsumerImpl implements UserToCouponConsumer {
    private final UserToCouponConsumerService userToCouponConsumerService;

    public UserToCouponConsumerImpl(UserToCouponConsumerService userToCouponConsumerService) {
        this.userToCouponConsumerService = userToCouponConsumerService;
    }

    private static final String RESPONSE_TOPIC_REPLY = "Issuance-of-coupons-reply";

    @Override
    @KafkaListener(groupId = "couponFIFO", topics = RESPONSE_TOPIC_REPLY)
    public void consume(@Payload KafkaUserInfoMessage message) {
        userToCouponConsumerService.createUserCoupon(message);
    }
}
