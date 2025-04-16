package com.taken_seat.auth_service.application.kafka.coupon;

import com.taken_seat.common_service.message.KafkaUserInfoMessage;

public interface UserToCouponConsumerService {

    void createUserCoupon(KafkaUserInfoMessage message);
}
