package com.taken_seat.auth_service.application.service.user;

import com.taken_seat.auth_service.domain.entity.mileage.Mileage;
import com.taken_seat.auth_service.domain.entity.user.User;
import com.taken_seat.auth_service.domain.entity.user.UserCoupon;
import com.taken_seat.auth_service.domain.repository.mileage.MileageRepository;
import com.taken_seat.auth_service.domain.repository.user.UserRepository;
import com.taken_seat.auth_service.domain.repository.userCoupon.UserCouponRepository;
import com.taken_seat.common_service.exception.customException.AuthException;
import com.taken_seat.common_service.exception.customException.CouponException;
import com.taken_seat.common_service.exception.customException.MileageException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import com.taken_seat.common_service.message.KafkaUserInfoMessage;
import com.taken_seat.common_service.message.UserBenefitMessage;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final UserRepository userRepository;
    private final UserCouponRepository userCouponRepository;

    private static final String REQUEST_TOPIC = "Issuance-of-coupons";
    private static final String REQUEST_KEY = "Partitions-of-coupons";
    private final MileageRepository mileageRepository;

    public KafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate, UserRepository userRepository,
                                UserCouponRepository userCouponRepository, MileageRepository mileageRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.userRepository = userRepository;
        this.userCouponRepository = userCouponRepository;
        this.mileageRepository = mileageRepository;
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

    @Transactional
    public UserBenefitMessage benefitUsage(UserBenefitMessage message) {
        try {
            User user = userRepository.findByIdAndDeletedAtIsNull(message.getUserId())
                    .orElseThrow(() -> new AuthException(ResponseCode.USER_NOT_FOUND));

            Integer couponDiscount = null;
            Integer usedMileage = null;

            if (message.getCouponId() != null) {
                UserCoupon userCoupon = userCouponRepository.findByCouponIdAndIsActiveTrue(message.getCouponId())
                        .orElseThrow(()->new CouponException(ResponseCode.COUPON_NOT_FOUND));
                if (userCoupon != null) {
                    couponDiscount = userCoupon.getDiscount();
                    userCoupon.updateActive(false, user.getId());
                }
            }
            if (message.getMileage() != null && message.getMileage() > 0) {
                Mileage mileages = mileageRepository.findTopByUserIdOrderByUpdatedAtDesc(message.getUserId())
                        .orElseThrow(()->new MileageException(ResponseCode.MILEAGE_NOT_FOUND));

                if (mileages != null) {
                    Integer currentMileage = mileages.getMileage() - message.getMileage();
                    if (currentMileage < 0) {
                        throw new IllegalArgumentException("사용 가능한 마일리지가 부족합니다.");
                    }
                    Mileage mileage = Mileage.create(
                            user, currentMileage
                    );
                    if (mileage.getCreatedBy() != null){
                        mileage.preUpdate(user.getId());
                    }
                    mileageRepository.save(mileage);
                    usedMileage = message.getMileage();
                }
            }
            return UserBenefitMessage.builder()
                    .paymentId(message.getPaymentId())
                    .userId(user.getId())
                    .couponId(message.getCouponId())
                    .mileage(usedMileage)
                    .discount(couponDiscount)
                    .status(UserBenefitMessage.UserBenefitStatus.SUCCESS)
                    .build();
        } catch (Exception e) {
            return UserBenefitMessage.builder()
                    .paymentId(message.getPaymentId())
                    .userId(message.getUserId())
                    .couponId(message.getCouponId())
                    .mileage(message.getMileage())
                    .status(UserBenefitMessage.UserBenefitStatus.FAIL)
                    .build();
        }
    }
}