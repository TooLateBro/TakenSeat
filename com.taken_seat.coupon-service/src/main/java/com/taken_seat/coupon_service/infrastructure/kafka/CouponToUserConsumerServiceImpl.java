package com.taken_seat.coupon_service.infrastructure.kafka;

import com.taken_seat.common_service.exception.customException.CouponException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import com.taken_seat.common_service.message.KafkaUserInfoMessage;
import com.taken_seat.coupon_service.application.kafka.CouponToUserConsumerService;
import com.taken_seat.coupon_service.domain.entity.Coupon;
import com.taken_seat.coupon_service.domain.repository.CouponRepository;
import com.taken_seat.coupon_service.infrastructure.config.redis.RedisOperationService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class CouponToUserConsumerServiceImpl implements CouponToUserConsumerService {

    private final CouponRepository couponRepository;
    private final RedisOperationService redisOperationService;
    private final RedissonClient redissonClient;

    public CouponToUserConsumerServiceImpl(CouponRepository couponRepository,
                                           RedisOperationService redisOperationService,
                                           RedissonClient redissonClient) {
        this.couponRepository = couponRepository;
        this.redisOperationService = redisOperationService;
        this.redissonClient = redissonClient;
    }

    @Transactional
    @Override
    public KafkaUserInfoMessage producerMessage(KafkaUserInfoMessage message) {
        UUID couponId = message.getCouponId();
        UUID userId = message.getUserId();
        String redisKey = "couponId:" + couponId;
        String issuedUserKey = "issuedUsers:" + couponId;
        String lockKey = "lock:coupon:" + couponId;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            if (lock.tryLock(1, 3, TimeUnit.SECONDS)) {
                try {
                    Coupon coupon = couponRepository.findByIdAndDeletedAtIsNull(couponId)
                            .orElseThrow(() -> new CouponException(ResponseCode.COUPON_NOT_FOUND));

                    if (!redisOperationService.hasKey(redisKey)) {
                        redisOperationService.initializeQuantity(redisKey, coupon.getQuantity());
                    }
                    Long currentQuantity = redisOperationService.getCurrentQuantity(redisKey);
                    if (currentQuantity == null || currentQuantity <= 0) {
                        log.warn("쿠폰이 모두 소진되었습니다. couponId: {}, currentQuantity: {}", couponId, currentQuantity);
                        return createFailedMessage(message);
                    }
                    // Lua 스크립트 실행
                    Long updatedQuantity = redisOperationService.evalScript(
                            redisKey, issuedUserKey, userId.toString());
                    if (updatedQuantity == -1) {
                        log.warn("쿠폰 수량이 부족합니다. couponId: {}", couponId);
                        throw new CouponException(ResponseCode.COUPON_QUANTITY_EXCEPTION);
                    }
                    if (updatedQuantity == -2) {
                        log.error("이미 발급에 성공한 유저입니다. userId: {}", userId);
                        throw new IllegalArgumentException("중복된 userId 입니다.");
                    }

                    coupon.updateQuantity(updatedQuantity, userId, coupon);
                    return createSuccessMessage(message, coupon);
                } finally {
                    lock.unlock();
                }
            } else {
                log.warn("락 획득 실패: couponId: {}", couponId);
                return createFailedMessage(message);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return createFailedMessage(message);
        } catch (Exception e) {
            return createFailedMessage(message);
        }
    }

    private KafkaUserInfoMessage createSuccessMessage(KafkaUserInfoMessage message, Coupon coupon) {
        message.success(
                message.getUserId(),
                message.getCouponId(),
                coupon.getDiscount(),
                coupon.getExpiredAt(),
                KafkaUserInfoMessage.Status.SUCCEEDED
        );
        log.info("쿠폰 발급 성공: couponId: {}, userId: {}", message.getCouponId(), message.getUserId());
        return message;
    }

    private KafkaUserInfoMessage createFailedMessage(KafkaUserInfoMessage message) {
        message.failed(
                message.getUserId(),
                message.getCouponId(),
                KafkaUserInfoMessage.Status.FAILED
        );
        log.info("쿠폰 발급 실패: couponId: {}, userId: {}", message.getCouponId(), message.getUserId());
        return message;
    }
}