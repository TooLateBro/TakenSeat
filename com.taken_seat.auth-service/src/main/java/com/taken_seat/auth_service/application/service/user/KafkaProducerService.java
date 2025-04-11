package com.taken_seat.auth_service.application.service.user;

import com.taken_seat.auth_service.application.dto.user.KafkaUserInfoMessage;
import com.taken_seat.auth_service.domain.entity.user.User;
import com.taken_seat.auth_service.domain.repository.user.UserRepository;
import com.taken_seat.common_service.message.KafkaUserCouponMessage;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final UserRepository userRepository;

    private static final String REQUEST_TOPIC = "Issuance-of-coupons";
    private static final String REQUEST_KEY = "Partitions-of-coupons";
    private static final String RESPONSE_TOPIC = "coupon-to-user";

    public KafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate, UserRepository userRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.userRepository = userRepository;
    }

    public KafkaUserInfoMessage sendUserCoupon(KafkaUserCouponMessage message) {
        User user = userRepository.findByIdAndDeletedAtIsNull(message.getUserId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        kafkaTemplate.send(REQUEST_TOPIC, REQUEST_KEY, message);
        return KafkaUserInfoMessage.of(user);
    }
}
