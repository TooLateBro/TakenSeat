package com.taken_seat.coupon_service.infrastructure.kafka;

import com.taken_seat.common_service.exception.customException.CouponException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import com.taken_seat.common_service.message.KafkaUserInfoMessage;
import com.taken_seat.coupon_service.application.kafka.CouponToUserConsumerService;
import com.taken_seat.coupon_service.domain.entity.Coupon;
import com.taken_seat.coupon_service.domain.repository.CouponRepository;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@Slf4j
public class CouponToUserConsumerServiceImpl implements CouponToUserConsumerService {

    private final CouponRepository couponRepository;
    private final RedisTemplate<String, Long> redisTemplate;
    private final RedissonClient redissonClient;

    public CouponToUserConsumerServiceImpl(CouponRepository couponRepository, RedisTemplate<String, Long> redisTemplate, RedissonClient redissonClient) {
        this.couponRepository = couponRepository;
        this.redisTemplate = redisTemplate;
        this.redissonClient = redissonClient;
    }

    @Transactional
    @Override
    public KafkaUserInfoMessage producerMessage(KafkaUserInfoMessage message) throws InterruptedException {
        String redisKey = "couponId:" + message.getCouponId();
//        RLock lock = redissonClient.getLock(redisKey); // redis key 로 락 설정
        try {
//            lock.tryLock(5, 2, TimeUnit.SECONDS); // 락 획득을 위한 최대 시간, 점유 최대 시간 설정 -> 5초, 2초
//            if (!lock.isLocked()){
//                throw new InterruptedException("대기 상태 입니다....");
//            }
            Coupon coupon = couponRepository.findByIdAndDeletedAtIsNull(message.getCouponId())
                    .orElseThrow(() -> new IllegalArgumentException("쿠폰이 존재하지 않습니다."));

            // hasKey 로 키 존재 여부 확인 후 set 으로 저장
            Boolean exists = redisTemplate.hasKey(redisKey); // 데이터 초기화
            if (exists== null || !exists) {
                redisTemplate.opsForValue().set(redisKey, coupon.getQuantity(), Duration.ofSeconds(10));
            }

            Long currentQuantity = redisTemplate.opsForValue().get(redisKey);

            if (currentQuantity == null || currentQuantity == 0) {
                throw new CouponException(ResponseCode.COUPON_QUANTITY_EXCEPTION);
            }
            Long updatedQuantity = redisTemplate.opsForValue().decrement(redisKey);

            if (updatedQuantity == 0 || 0 == coupon.getQuantity()) {
                throw new CouponException(ResponseCode.COUPON_QUANTITY_EXCEPTION);
            }

            coupon.updateQuantity(updatedQuantity, message.getUserId(), coupon);
//            redisTemplate.delete(redisKey);
            message.success(
                    message.getUserId(), message.getCouponId(), coupon.getDiscount(),
                    coupon.getExpiredAt(), KafkaUserInfoMessage.Status.SUCCEEDED
            );
        } catch (RuntimeException e) {
            message.failed(
                    message.getUserId(), message.getCouponId(), KafkaUserInfoMessage.Status.FAILED
            );
        }finally {
//            lock.unlock();
        }
        return message;
    }
}
