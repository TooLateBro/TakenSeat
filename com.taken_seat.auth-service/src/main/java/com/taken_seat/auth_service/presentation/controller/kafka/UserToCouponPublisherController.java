package com.taken_seat.auth_service.presentation.controller.kafka;

import com.taken_seat.auth_service.application.kafka.coupon.UserToCouponPublisher;
import com.taken_seat.auth_service.presentation.docs.UserToCouponPublisherControllerDocs;
import com.taken_seat.common_service.exception.customException.CouponException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import com.taken_seat.common_service.message.KafkaUserInfoMessage;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserToCouponPublisherController implements UserToCouponPublisherControllerDocs {

    private final UserToCouponPublisher userToCouponPublisher;

    public UserToCouponPublisherController(UserToCouponPublisher userToCouponPublisher) {
        this.userToCouponPublisher = userToCouponPublisher;
    }

    @PostMapping("/send")
    public void sendUserCoupon(@RequestBody KafkaUserInfoMessage message){
        try {
            userToCouponPublisher.sendUserCoupon(message);
        } catch (Exception e) {
            throw new CouponException(ResponseCode.COUPON_QUANTITY_EXCEPTION);
        }
    }
}
