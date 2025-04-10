package com.taken_seat.auth_service.application.service.user;

import com.taken_seat.auth_service.application.dto.user.KafkaUserInfoMessage;
import com.taken_seat.auth_service.domain.entity.user.User;
import com.taken_seat.auth_service.domain.repository.user.UserRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class KafkaUserService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final UserRepository userRepository;

    private static final String REQUEST_TOPIC = "user-to-coupon";
    private static final String RESPONSE_TOPIC = "coupon-to-user";

    public KafkaUserService(KafkaTemplate<String, Object> kafkaTemplate, UserRepository userRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.userRepository = userRepository;
    }

    public KafkaUserInfoMessage sendUserCoupon(UUID userId) {
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        kafkaTemplate.send(REQUEST_TOPIC, String.valueOf(userId), user.getUserCoupons());
        return KafkaUserInfoMessage.of(user);
    }
}
