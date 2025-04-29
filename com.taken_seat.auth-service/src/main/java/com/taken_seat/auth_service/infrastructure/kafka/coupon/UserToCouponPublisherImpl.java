package com.taken_seat.auth_service.infrastructure.kafka.coupon;

import com.taken_seat.auth_service.application.kafka.coupon.UserToCouponPublisher;
import com.taken_seat.common_service.message.KafkaUserInfoMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserToCouponPublisherImpl implements UserToCouponPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.coupon-request-user}")
    private String REQUEST_TOPIC;

    public UserToCouponPublisherImpl(
            KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void sendUserCoupon(KafkaUserInfoMessage message) {
        try {
            String partitionKey = "couponId : " + message.getCouponId();

            kafkaTemplate.send(REQUEST_TOPIC, partitionKey, message)
                    .whenComplete((sendResult, ex) -> {
                        try {
                            if (ex != null) {
                                log.error("[Auth] -> [Coupon] 쿠폰 발급 요청 실패 - key: {}, error: {}",
                                        partitionKey, ex.getMessage());
                            } else {
                                log.info("[Auth] -> [Coupon] 쿠폰 발급 요청 성공 - partition: {}, key: {}",
                                        sendResult.getRecordMetadata().partition(), partitionKey);
                            }
                        }catch (Exception e) {
                            log.error("[Auth] 서버 오류");
                        }
                    });
        } catch (Exception e) {
            log.error("[쿠폰요청] 예외 발생 - userId: {}, couponId: {}, 오류: {}",
                    message.getUserId(), message.getCouponId(), e.getMessage());
        }
    }
}