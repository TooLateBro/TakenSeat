package com.taken_seat.coupon_service.application.service;

import com.taken_seat.common_service.exception.customException.CouponException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import com.taken_seat.common_service.message.KafkaUserInfoMessage;
import com.taken_seat.coupon_service.domain.entity.Coupon;
import com.taken_seat.coupon_service.domain.repository.CouponRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class KafkaProducerService {

    private final CouponRepository couponRepository;
    private final RedisTemplate<String, Long> redisTemplate;

    public KafkaProducerService(CouponRepository couponRepository, RedisTemplate<String, Long> redisTemplate) {
        this.couponRepository = couponRepository;
        this.redisTemplate = redisTemplate;
    }

    @Transactional
    public KafkaUserInfoMessage producerMessage(KafkaUserInfoMessage message) {
        try {
            Coupon coupon = couponRepository.findByIdAndDeletedAtIsNull(message.getCouponId())
                    .orElseThrow(() -> new IllegalArgumentException("쿠폰이 존재하지 않습니다."));

            String redisKey = "coupon_" + message.getCouponId() + ":quantity";
            // hasKey 로 키 존재 여부 확인 후 set 으로 저장
            Boolean exists = redisTemplate.hasKey(redisKey); // 데이터 초기화
            if (!exists) {
                redisTemplate.opsForValue().set(redisKey, coupon.getQuantity());
            }

            Long currentQuantity = redisTemplate.opsForValue().get(redisKey);

            if (currentQuantity == null || currentQuantity == 0) {
                throw new CouponException(ResponseCode.COUPON_QUANTITY_EXCEPTION);
            }

            Long updatedQuantity = redisTemplate.opsForValue().decrement(redisKey);

            if (updatedQuantity == 0 || 0 == coupon.getQuantity()) {
                throw new CouponException(ResponseCode.COUPON_QUANTITY_EXCEPTION);
            }
            coupon.updateQuantity(updatedQuantity, message.getUserId());
            redisTemplate.delete(redisKey);
            message.setStatus(KafkaUserInfoMessage.Status.SUCCEEDED);

        } catch (RuntimeException e) {
            message.setStatus(KafkaUserInfoMessage.Status.FAILED);
            throw new CouponException(ResponseCode.COUPON_QUANTITY_EXCEPTION, e.getMessage());
        }
        return message;
    }
}
