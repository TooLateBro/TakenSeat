package com.taken_seat.auth_service.infrastructure.kafka.coupon;

import com.taken_seat.auth_service.application.kafka.coupon.UserToCouponPublisher;
import com.taken_seat.auth_service.domain.repository.user.UserRepository;
import com.taken_seat.common_service.exception.customException.AuthException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import com.taken_seat.common_service.message.KafkaUserInfoMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserToCouponPublisherImpl implements UserToCouponPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final UserRepository userRepository;

    public UserToCouponPublisherImpl(KafkaTemplate<String, Object> kafkaTemplate, UserRepository userRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.userRepository = userRepository;
    }
    @Value("${kafka.topic.coupon-request-user}")
    private String REQUEST_TOPIC;

    @Value("${kafka.key.coupon-user-key}")
    private String REQUEST_KEY;

    @Override
    public void sendUserCoupon(KafkaUserInfoMessage message) {
        userRepository.findByIdAndDeletedAtIsNull(message.getUserId())
                .orElseThrow(() -> new AuthException(ResponseCode.USER_NOT_FOUND));

        kafkaTemplate.send(REQUEST_TOPIC,  REQUEST_KEY, message)
                .thenAccept(sendResult -> {
                    log.info("[Auth] -> [Coupon] 쿠폰 발급 요청에 성공했습니다! : {}, {}", message.getUserId(), message.getCouponId());
                }).exceptionally(exception -> {
                    log.error("[Auth] -> [Coupon] 쿠폰 발급 요청에 실패했습니다! : {}, {}", message.getUserId(), message.getCouponId());
                    return null;
                });

    }
}
