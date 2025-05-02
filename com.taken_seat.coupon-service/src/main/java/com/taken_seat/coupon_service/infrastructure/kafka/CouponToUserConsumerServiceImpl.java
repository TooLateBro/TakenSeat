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
    private final RedisTemplate<String, Long> longRedisTemplate;
    private final RedisTemplate<String, Object> objectRedisTemplate;
    private final RedissonClient redissonClient;

    public CouponToUserConsumerServiceImpl(CouponRepository couponRepository,
                                           RedisTemplate<String, Long> longRedisTemplate,
                                           RedisTemplate<String, Object> objectRedisTemplate,
                                           RedissonClient redissonClient) {
        this.couponRepository = couponRepository;
        this.longRedisTemplate = longRedisTemplate;
        this.objectRedisTemplate = objectRedisTemplate;
        this.redissonClient = redissonClient;
    }

    @Transactional
    @Override
    public KafkaUserInfoMessage producerMessage(KafkaUserInfoMessage message){
        String redisKey = "couponId : " + message.getCouponId();
//        RLock lock = redissonClient.getLock(redisKey); // redis key 로 락 설정
        try {
//            lock.tryLock(5, 2, TimeUnit.SECONDS); // 락 획득을 위한 최대 시간, 점유 최대 시간 설정 -> 5초, 2초
//            if (!lock.isLocked()){
//                throw new InterruptedException("대기 상태 입니다....");
//            }
            Coupon coupon = couponRepository.findByIdAndDeletedAtIsNull(message.getCouponId())
                    .orElseThrow(() -> new CouponException(ResponseCode.COUPON_NOT_FOUND));

            // hasKey 로 키 존재 여부 확인 후 set 으로 저장
            Boolean exists = longRedisTemplate.hasKey(redisKey); // 데이터 초기화
            if (exists == null || !exists) {
                longRedisTemplate.opsForValue().set(redisKey, coupon.getQuantity(), Duration.ofSeconds(60));
            }

            Long currentQuantity = longRedisTemplate.opsForValue().get(redisKey);

            if (currentQuantity == null || currentQuantity == 0) {
                throw new CouponException(ResponseCode.COUPON_QUANTITY_EXCEPTION);
            }
            Long updatedQuantity = longRedisTemplate.opsForValue().decrement(redisKey);

            if (updatedQuantity == 0 || updatedQuantity < 0) {
                throw new CouponException(ResponseCode.COUPON_QUANTITY_EXCEPTION);
            }
            String issuedUserKey = "issuedUsers : " + message.getCouponId();
            Boolean existsUser = objectRedisTemplate.opsForSet().isMember(issuedUserKey, message.getUserId());
            if (existsUser != null && existsUser) {
                log.error("이미 발급에 성공한 유저입니다. userId : " + message.getUserId());
                throw new IllegalArgumentException("중복된 userId 입니다.");
            }
            objectRedisTemplate.opsForSet().add(issuedUserKey, message.getUserId());
            objectRedisTemplate.expire(issuedUserKey, Duration.ofMinutes(1));

            coupon.updateQuantity(updatedQuantity, message.getUserId(), coupon);
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
