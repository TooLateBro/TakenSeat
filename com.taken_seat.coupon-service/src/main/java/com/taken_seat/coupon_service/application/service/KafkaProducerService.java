package com.taken_seat.coupon_service.application.service;

import com.taken_seat.common_service.exception.customException.CouponException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import com.taken_seat.common_service.message.KafkaUserInfoMessage;
import com.taken_seat.coupon_service.domain.entity.Coupon;
import com.taken_seat.coupon_service.domain.repository.CouponRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaProducerService {

    private final CouponRepository couponRepository;
    private final RedisTemplate<String, Integer> redisTemplate;

    public KafkaProducerService(CouponRepository couponRepository, RedisTemplate<String, Integer> redisTemplate) {
        this.couponRepository = couponRepository;
        this.redisTemplate = redisTemplate;
    }

    public KafkaUserInfoMessage producerMessage(KafkaUserInfoMessage message) {
        try {
            Coupon coupon = couponRepository.findByIdAndDeletedAtIsNull(message.getCouponId())
                    .orElseThrow(() -> new IllegalArgumentException("쿠폰이 존재하지 않습니다."));

            String redisKey = "coupon " + message.getCouponId() + ":quantity";
            // hasKey 로 키 존재 여부 확인 후 set 으로 저장
            Boolean exists = redisTemplate.hasKey(redisKey); // 데이터 초기화
            if (exists == null || !exists) {
                redisTemplate.opsForValue().set(redisKey, coupon.getQuantity());
            }

            Integer currentQuantity = redisTemplate.opsForValue().get(redisKey);

            if (currentQuantity == null || currentQuantity == 0) {
                throw new CouponException(ResponseCode.COUPON_QUANTITY_EXCEPTION);
            }

            Long updatedQuantity = redisTemplate.opsForValue().decrement(redisKey);

            if (updatedQuantity == null || updatedQuantity == 0) {
                throw new CouponException(ResponseCode.COUPON_QUANTITY_EXCEPTION);
            }
            message.setStatus(KafkaUserInfoMessage.Status.SUCCEEDED);

        } catch (RuntimeException e) {
            message.setStatus(KafkaUserInfoMessage.Status.FAILED);
            throw new CouponException(ResponseCode.COUPON_QUANTITY_EXCEPTION, e.getMessage());
        }
        return message;
    }
}
