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

    private static final String LUA_SCRIPT = """
            -- KEYS[1] : 쿠폰 수량을 저장하는 Redis 키
            -- KEYS[2] : 이미 쿠폰을 발급받은 유저들을 저장하는 Redis Set 키
            -- ARGV[1] : 발급하려는 유저의 ID
            local couponKey = KEYS[1]   -- 스크립트가 실행될 때 전달받은 첫 번째 키
            local userKey = KEYS[2]  -- Redis Set의 키
            local userId = ARGV[1]   -- 스크립트 실행 시 전달받은 첫 번째 인수 -> 동적인 값
            
            local current = redis.call('GET', couponKey)
            
            -- tonumber : 값을 숫자로 변환하는 함수
            if current == nil or tonumber(current) <= 0 then
                return -1  -- -1을 반환하여 쿠폰 수량 부족을 알림
            end
            
            -- SISMEMBER : Redis의 Set 자료형에서 특정 값이 존재하는지 확인
            if redis.call('SISMEMBER', userKey, userId) == 1 then
                return -2  -- -2를 반환하여 이미 발급된 유저임을 알림
            end
            redis.call('DECR', couponKey)
            redis.call('SADD', userKey, userId)
            
            -- 쿠폰 발급 후, 남은 수량을 반환 (현재 수량에서 하나 감소한 값)
            return tonumber(current) - 1
            """;

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
                            LUA_SCRIPT, redisKey, issuedUserKey, userId.toString());
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