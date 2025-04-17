package com.taken_seat.auth_service.infrastructure.kafka.booking;

import com.taken_seat.auth_service.application.kafka.booking.UserToBookingConsumerService;
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
import com.taken_seat.common_service.message.UserBenefitMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class UserToBookingConsumerServiceImpl implements UserToBookingConsumerService {

    private final UserRepository userRepository;
    private final UserCouponRepository userCouponRepository;
    private final MileageRepository mileageRepository;

    public UserToBookingConsumerServiceImpl(UserRepository userRepository,
                                            UserCouponRepository userCouponRepository, MileageRepository mileageRepository) {
        this.userRepository = userRepository;
        this.userCouponRepository = userCouponRepository;
        this.mileageRepository = mileageRepository;
    }

    @Override
    public UserBenefitMessage benefitUsage(UserBenefitMessage message) {
        try {
            log.info("[Booking] -> [Auth] 마일리지 및 쿠폰을 체크 중 입니다...." +
                    "{}, {}, {}, {}", message.getBookingId(), message.getUserId(), message.getCouponId(), message.getMileage());
            User user = userRepository.findByIdAndDeletedAtIsNull(message.getUserId())
                    .orElseThrow(() -> new AuthException(ResponseCode.USER_NOT_FOUND));

            Integer couponDiscount = null;
            Integer usedMileage = null;

            UserCoupon userCoupon = userCouponRepository.findByCouponIdAndIsActiveTrue(message.getCouponId())
                    .orElseThrow(() -> new CouponException(ResponseCode.COUPON_NOT_FOUND));

            couponDiscount = userCoupon.getDiscount();

            Mileage mileage = mileageRepository.findTopByUserIdOrderByUpdatedAtDesc(message.getUserId())
                    .orElseThrow(() -> new MileageException(ResponseCode.MILEAGE_NOT_FOUND));

            usedMileage = mileage.getMileage();

            log.info("[Auth] -> [Booking] 마일리지 및 쿠폰 사용에 성공했습니다! " +
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
            log.info("[Auth] -> [Booking] 마일리지 및 쿠폰 사용에 실패하였습니다. " +
                    "{}, {}, {}, {}", message.getBookingId(), message.getUserId(), message.getCouponId(), message.getMileage());
            return UserBenefitMessage.builder()
                    .status(UserBenefitMessage.UserBenefitStatus.FAIL)
                    .build();
        }
    }

    @Transactional
    @Override
    public void benefitPayment(UserBenefitMessage message) {
        log.info("[Booking] -> [Auth] 마일리지 및 쿠폰을 성공 유무를 체크 중 입니다...." +
                "{}, {}, {}, {}", message.getBookingId(), message.getUserId(), message.getCouponId(), message.getMileage());
        User user = userRepository.findByIdAndDeletedAtIsNull(message.getUserId())
                .orElseThrow(() -> new AuthException(ResponseCode.USER_NOT_FOUND));

        Integer mileageRate = message.getPrice() / 1000;

        if (message.getStatus().equals(UserBenefitMessage.UserBenefitStatus.SUCCESS)) {
            UserCoupon userCoupon = userCouponRepository.findByCouponIdAndIsActiveTrue(message.getCouponId())
                    .orElseThrow(() -> new CouponException(ResponseCode.COUPON_NOT_FOUND));
            if (userCoupon != null) {
                log.info("[Booking] -> [Auth] " +message.getCouponId() + " 쿠폰 사용에 성공했습니다!!!");
                userCoupon.updateActive(false, user.getId());
            }

            Mileage mileageExists = mileageRepository.findTopByUserIdOrderByUpdatedAtDesc(message.getUserId())
                    .orElseThrow(() -> new MileageException(ResponseCode.MILEAGE_NOT_FOUND));

            if (mileageExists != null) {
                Integer currentMileage = mileageExists.getMileage() - message.getMileage() + mileageRate;

                log.info("[Booking] -> [Auth] " +message.getMileage() + " 마일리지 사용에 성공했습니다!!!");
                if (currentMileage < 0) {
                    throw new MileageException(ResponseCode.MILEAGE_EMPTY);
                }
                Mileage mileage = Mileage.create(
                        user, currentMileage
                );
                if (mileage.getCreatedBy() != null) {
                    mileage.preUpdate(user.getId());
                }
                mileageRepository.save(mileage);
            }
        }else if (message.getStatus().equals(UserBenefitMessage.UserBenefitStatus.REFUND)) {
            if (message.getCouponId() != null) {
                UserCoupon userCoupon = userCouponRepository.findByCouponIdAndIsActiveFalse(message.getCouponId())
                        .orElseThrow(() -> new CouponException(ResponseCode.COUPON_NOT_FOUND));
                if (userCoupon != null) {
                    userCoupon.updateActive(true, user.getId());
                    log.info("[Auth] 쿠폰 활성화 완료! {}, {}", message.getCouponId(), userCoupon.isActive());
                }
            }

            if (message.getMileage() != null && message.getMileage() > 0) {
                Mileage mileages = mileageRepository.findTopByUserIdOrderByUpdatedAtDesc(message.getUserId())
                        .orElseThrow(() -> new MileageException(ResponseCode.MILEAGE_NOT_FOUND));

                if (mileages != null) {
                    Integer currentMileage = mileages.getMileage() + message.getMileage() - mileageRate;
                    if (currentMileage < 0) {
                        throw new MileageException(ResponseCode.MILEAGE_EMPTY);
                    }
                    Mileage mileage = Mileage.create(
                            user, currentMileage
                    );
                    if (mileage.getCreatedBy() != null) {
                        mileage.preUpdate(user.getId());
                    }
                    log.info("[Auth] 마일리지 복원 완료! {}", message.getMileage());
                    mileageRepository.save(mileage);
                }
            }
        }
    }
}