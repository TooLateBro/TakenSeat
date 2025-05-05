package com.taken_seat.coupon_service.infrastructure.kafka;

import com.taken_seat.common_service.exception.customException.CouponException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
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

    @KafkaListener(groupId = "${kafka.consumer.group-id}", topics = "${kafka.topic.coupon-request-user}")
    @Override
    @SendTo("coupon.response.user")
    public KafkaUserInfoMessage consume(@Payload KafkaUserInfoMessage message,
                                        @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        if (!key.equals("couponId : " + message.getCouponId())){
            throw new CouponException(ResponseCode.COUPON_NOT_FOUND);
        }
        return couponToUserConsumerService.producerMessage(message);
    }
}