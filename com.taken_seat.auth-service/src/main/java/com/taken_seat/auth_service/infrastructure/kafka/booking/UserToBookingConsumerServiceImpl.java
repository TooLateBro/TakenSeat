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
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
public class UserToBookingConsumerServiceImpl implements UserToBookingConsumerService {

    private final UserRepository userRepository;
    private final UserCouponRepository userCouponRepository;
    private final MileageRepository mileageRepository;
    private final MeterRegistry meterRegistry;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.benefit-refund-response}")
    private String BENEFIT_REFUND_RESPONSE;

    public UserToBookingConsumerServiceImpl(UserRepository userRepository,
                                            UserCouponRepository userCouponRepository, MileageRepository mileageRepository,
                                            MeterRegistry meterRegistry, KafkaTemplate<String, Object> kafkaTemplate) {
        this.userRepository = userRepository;
        this.userCouponRepository = userCouponRepository;
        this.mileageRepository = mileageRepository;
        this.meterRegistry = meterRegistry;
        this.kafkaTemplate = kafkaTemplate;
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
            if (message.getCouponId() != null) {
                UserCoupon userCoupon = userCouponRepository.findByCouponIdAndIsActiveTrue(message.getCouponId())
                        .orElseThrow(() -> new CouponException(ResponseCode.COUPON_NOT_FOUND));
                if (userCoupon.getExpiredAt().isBefore(LocalDateTime.now())){
                    log.error("[Auth] 쿠폰을 사용할 수 없습니다. couponId={}, expiredAt={}", message.getCouponId(), userCoupon.getExpiredAt());
                }else {
                    couponDiscount = userCoupon.getDiscount();
                }
            }
            if (message.getMileage() != null) {
                mileageRepository.findTopByUserIdOrderByUpdatedAtDesc(message.getUserId())
                        .orElseThrow(() -> new MileageException(ResponseCode.MILEAGE_NOT_FOUND));

                usedMileage = message.getMileage();
            }
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
            log.error("[Auth] -> [Booking] 마일리지 및 쿠폰 사용에 실패하였습니다. " +
                    "{}, {}, {}, {}", message.getBookingId(), message.getUserId(), message.getCouponId(), message.getMileage());
            return UserBenefitMessage.builder()
                    .status(UserBenefitMessage.UserBenefitStatus.FAIL)
                    .build();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    @Counted(value = "benefit.usage.coupon", description = "쿠폰 사용량 카운트")
    public void benefitPayment(UserBenefitMessage message) {
        log.info("[Booking] -> [Auth] 마일리지 및 쿠폰의 성공 유무를 체크 중 입니다...." +
                "{}, {}, {}, {}", message.getBookingId(), message.getUserId(), message.getCouponId(), message.getMileage());
        User user = userRepository.findByIdAndDeletedAtIsNull(message.getUserId())
                .orElseThrow(() -> new AuthException(ResponseCode.USER_NOT_FOUND));

        int mileageRate = message.getPrice() != null ? message.getPrice() / 1000 : 0;

        if (message.getStatus().equals(UserBenefitMessage.UserBenefitStatus.SUCCESS)) {
            try {
                Mileage mileageExists = mileageRepository.findTopByUserIdOrderByUpdatedAtDesc(message.getUserId())
                        .orElseThrow(() -> new MileageException(ResponseCode.MILEAGE_NOT_FOUND));
                UserCoupon userCoupon = userCouponRepository.findByCouponIdAndIsActiveTrue(message.getCouponId())
                        .orElseThrow(() -> new CouponException(ResponseCode.COUPON_NOT_FOUND));

                log.info("[Auth] 현재 보유중인 마일리지 및 쿠폰의 상태 : {}, {}",
                        mileageExists.getMileage(), userCoupon.isActive());

                userCoupon.updateActive(false, user.getId());
                log.info("[Auth] {} 쿠폰 사용에 성공했습니다!!!", message.getCouponId());
                meterRegistry.counter("benefit.usage.count", "status", "success", "reason", "쿠폰 사용에 성공했습니다!").increment();

                log.info("[Auth] {} 마일리지 사용에 성공했습니다!!!", message.getMileage());

                int currentMileage = mileageExists.getMileage() - message.getMileage() + mileageRate;
                log.info("[Auth] {} 마일리지 적립에 성공했습니다!!!!", mileageRate);
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
            } catch (MileageException | CouponException e) {
                log.error("[Auth] 마일리지 또는 쿠폰 처리 중 오류가 발생했습니다 : {}", e.getMessage());
                meterRegistry.counter("benefit.usage.count", "status", "fail", "reason", "쿠폰 사용에 실패했습니다.").increment();
            } catch (Exception e) {
                log.error("[Auth] 서비스에서 예기치 않은 오류가 발생했습니다 : {}", e.getMessage());
                meterRegistry.counter("benefit.usage.count", "status", "fail", "reason", "쿠폰 사용에 실패했습니다.").increment();
            }
        } else if (message.getStatus().equals(UserBenefitMessage.UserBenefitStatus.REFUND)) {
            try {
                log.info("[Booking] -> [Auth] 환불 처리 중...." +
                        "{}, {}, {}, {}", message.getBookingId(), message.getUserId(), message.getCouponId(), message.getMileage());
                UserCoupon userCoupon = userCouponRepository.findByCouponIdAndIsActiveFalse(message.getCouponId())
                        .orElseThrow(() -> new CouponException(ResponseCode.COUPON_NOT_FOUND));

                userCoupon.updateActive(true, user.getId());
                log.info("[Auth] 쿠폰 활성화 완료! {}, {}", message.getCouponId(), userCoupon.isActive());

                if (message.getMileage() != null && message.getMileage() > 0) {
                    Mileage mileages = mileageRepository.findTopByUserIdOrderByUpdatedAtDesc(message.getUserId())
                            .orElseThrow(() -> new MileageException(ResponseCode.MILEAGE_NOT_FOUND));

                    int currentMileage = mileages.getMileage() + message.getMileage() - mileageRate;
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
                log.info("[Auth] -> [Booking] 환불 요청에 성공했습니다!!!");

                UserBenefitMessage benefitMessage = UserBenefitMessage.builder()
                        .bookingId(message.getBookingId())
                        .userId(user.getId())
                        .status(UserBenefitMessage.UserBenefitStatus.SUCCESS)
                        .build();

                kafkaTemplate.send(BENEFIT_REFUND_RESPONSE, benefitMessage);

            } catch (MileageException | CouponException e) {
                log.error("[Auth] 환불 처리 중 오류가 발생했습니다 : {}", e.getMessage());
            } catch (Exception e) {
                log.error("[Auth] 서비스에서 예기치 오류가 발생했습니다 : {}", e.getMessage());
            }
        }
    }
}