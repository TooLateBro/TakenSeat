package com.taken_seat.coupon_service.application.kafka;

import com.taken_seat.common_service.message.KafkaUserInfoMessage;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

public interface CouponToUserConsumer {

    KafkaUserInfoMessage consume(@Payload KafkaUserInfoMessage message,
                                 @Header(KafkaHeaders.RECEIVED_KEY) String key);
}
