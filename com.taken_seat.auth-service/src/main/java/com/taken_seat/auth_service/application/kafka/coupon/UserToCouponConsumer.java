package com.taken_seat.auth_service.application.kafka.coupon;

import com.taken_seat.common_service.message.KafkaUserInfoMessage;
import org.springframework.messaging.handler.annotation.Payload;

public interface UserToCouponConsumer {

    void consume(@Payload KafkaUserInfoMessage message);
}
