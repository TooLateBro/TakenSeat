package com.taken_seat.auth_service.infrastructure.kafka.coupon;

import com.taken_seat.auth_service.application.kafka.coupon.UserToCouponConsumerService;
import com.taken_seat.auth_service.domain.entity.user.User;
import com.taken_seat.auth_service.domain.entity.user.UserCoupon;
import com.taken_seat.auth_service.domain.repository.user.UserRepository;
import com.taken_seat.auth_service.domain.repository.userCoupon.UserCouponRepository;
import com.taken_seat.common_service.exception.customException.AuthException;
import com.taken_seat.common_service.exception.customException.CouponException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import com.taken_seat.common_service.message.KafkaUserInfoMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserToCouponConsumerServiceImpl implements UserToCouponConsumerService {

    private final UserRepository userRepository;
    private final UserCouponRepository userCouponRepository;

    public UserToCouponConsumerServiceImpl(UserRepository userRepository,
                                           UserCouponRepository userCouponRepository) {
        this.userRepository = userRepository;
        this.userCouponRepository = userCouponRepository;
    }

    @Override
    @CacheEvict(cacheNames = "searchCache", allEntries = true)
    public void createUserCoupon(KafkaUserInfoMessage message) {
        userCouponRepository.findByUserIdAndCouponIdAndDeletedAtIsNull(message.getUserId(), message.getCouponId())
                .ifPresent(coupon -> {
                    throw new CouponException(ResponseCode.COUPON_HAS_USER);
                });

        User user = userRepository.findByIdAndDeletedAtIsNull(message.getUserId())
                .orElseThrow(() -> new AuthException(ResponseCode.USER_NOT_FOUND));

        if (message.getStatus() == KafkaUserInfoMessage.Status.SUCCEEDED) {
            UserCoupon u_c = UserCoupon.create(user, message);

            userCouponRepository.save(u_c);
            log.info("[Coupon] -> [Auth] 쿠폰 발급에 성공하였습니다! 마이페이지에서 확인해주세요. {}, {}", message.getUserId(), message.getCouponId());
        }else{
            log.error("[Auth] 쿠폰이 모두 소진되었습니다.");
        }
    }
}
