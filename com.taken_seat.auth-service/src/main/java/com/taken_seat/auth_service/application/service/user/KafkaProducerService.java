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
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
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

        kafkaTemplate.send(REQUEST_TOPIC, REQUEST_KEY, message)
                .thenAccept(sendResult -> {
                    log.info("<Auth> -> <Coupon> 쿠폰 발급 요청에 성공했습니다! : {}, {}", message.getUserId(), message.getCouponId());
                }).exceptionally(exception -> {
                    log.error("<Auth> -> <Coupon> 쿠폰 발급 요청에 실패했습니다! : {}, {}", message.getUserId(), message.getCouponId());
                    return null;
                });

    }

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
            log.info("<Coupon> -> <Auth> 쿠폰 발급에 성공하였습니다! 마이페이지에서 확인해주세요. {}, {}", message.getUserId(), message.getCouponId());
        }else{
            log.error("<Auth> 쿠폰이 모두 소진되었습니다.");
        }
    }

    @Transactional
    public UserBenefitMessage benefitUsage(UserBenefitMessage message) {
        try {
            log.info("<Booking> -> <Auth> 마일리지 및 쿠폰 사용 여부를 체크 중 입니다...." +
                    "{}, {}, {}, {}", message.getBookingId(), message.getUserId(), message.getCouponId(), message.getMileage());
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
                        throw new MileageException(ResponseCode.MILEAGE_EMPTY);
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
            log.info("<Auth> -> <Booking> 마일리지 및 쿠폰 사용 여부 검증에 성공했습니다! " +
                    "{}, {}, {}, {}, {}", message.getBookingId(), message.getUserId(), message.getCouponId(), message.getMileage(), couponDiscount);
            return UserBenefitMessage.builder()
                    .bookingId(message.getBookingId())
                    .userId(user.getId())
                    .couponId(message.getCouponId())
                    .mileage(usedMileage)
                    .discount(couponDiscount)
                    .status(UserBenefitMessage.UserBenefitStatus.SUCCESS)
                    .build();
        } catch (Exception e) {
            log.info("<Auth> -> <Booking> 마일리지 및 쿠폰 사용 여부 검증에 실패하였습니다. " +
                    "{}, {}, {}, {}", message.getBookingId(), message.getUserId(), message.getCouponId(), message.getMileage());
            return UserBenefitMessage.builder()
                    .bookingId(message.getBookingId())
                    .userId(message.getUserId())
                    .couponId(message.getCouponId())
                    .mileage(message.getMileage())
                    .status(UserBenefitMessage.UserBenefitStatus.FAIL)
                    .build();
        }
    }
}