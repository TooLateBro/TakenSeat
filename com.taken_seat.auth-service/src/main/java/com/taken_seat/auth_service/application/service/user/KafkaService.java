package com.taken_seat.auth_service.application.service.user;

import com.taken_seat.auth_service.domain.entity.user.User;
import com.taken_seat.auth_service.domain.entity.user.UserCoupon;
import com.taken_seat.auth_service.domain.repository.user.UserRepository;
import com.taken_seat.auth_service.domain.repository.userCoupon.UserCouponRepository;
import com.taken_seat.common_service.message.KafkaUserInfoMessage;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final UserRepository userRepository;
    private final UserCouponRepository userCouponRepository;

    private static final String REQUEST_TOPIC = "Issuance-of-coupons";
    private static final String REQUEST_KEY = "Partitions-of-coupons";

    public KafkaService(KafkaTemplate<String, Object> kafkaTemplate, UserRepository userRepository,
                        UserCouponRepository userCouponRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.userRepository = userRepository;
        this.userCouponRepository = userCouponRepository;
    }

    public void sendUserCoupon(KafkaUserInfoMessage message) {
        userRepository.findByIdAndDeletedAtIsNull(message.getUserId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        try {
            kafkaTemplate.send(REQUEST_TOPIC, REQUEST_KEY, message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @CacheEvict(cacheNames = "searchCache", allEntries = true)
    public void createUserCoupon(KafkaUserInfoMessage message) {
        userCouponRepository.findByUserIdAndCouponIdAndDeletedAtIsNull(message.getUserId(), message.getCouponId())
                .ifPresent(coupon -> {
                    throw new IllegalArgumentException("이미 등록된 쿠폰입니다.");
                });

        User user = userRepository.findByIdAndDeletedAtIsNull(message.getUserId())
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        if (message.getStatus() == KafkaUserInfoMessage.Status.SUCCEEDED) {
            UserCoupon u_c = UserCoupon.create(user, message);

            userCouponRepository.save(u_c);
        }
    }
}