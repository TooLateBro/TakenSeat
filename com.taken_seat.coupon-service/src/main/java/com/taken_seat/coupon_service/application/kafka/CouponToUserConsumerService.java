package com.taken_seat.coupon_service.application.kafka;

import com.taken_seat.common_service.message.KafkaUserInfoMessage;

public interface CouponToUserConsumerService {

    KafkaUserInfoMessage producerMessage(KafkaUserInfoMessage message) throws InterruptedException ;
}
