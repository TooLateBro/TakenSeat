package com.taken_seat.coupon_service.application.service;

import com.taken_seat.common_service.message.KafkaUserInfoMessage;
import com.taken_seat.coupon_service.domain.entity.Coupon;
import com.taken_seat.coupon_service.domain.repository.CouponRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaProducerService {

    private final CouponRepository couponRepository;

    public KafkaProducerService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    public KafkaUserInfoMessage producerMessage(KafkaUserInfoMessage message) {
        try {
            Coupon coupon = couponRepository.findByIdAndDeletedAtIsNull(message.getCouponId())
                    .orElseThrow(() -> new IllegalArgumentException("쿠폰이 존재하지 않습니다."));

            message.setStatus(KafkaUserInfoMessage.Status.SUCCEEDED);
            // 쿠폰 처리 로직 추가
            log.info("Processed coupon: {}", coupon);

        } catch (IllegalArgumentException e) {
            log.error("Coupon not found: {}", message.getCouponId());
            // 예외 처리 로직 추가
        } catch (Exception e) {
            log.error("Error processing coupon: {}", message.getCouponId(), e);
            // 예외 처리 로직 추가
        }
        return message;
    }
}
