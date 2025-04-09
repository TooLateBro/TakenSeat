package com.taken_seat.coupon_service.application.service;

import com.taken_seat.coupon_service.application.dto.CouponDto;
import com.taken_seat.coupon_service.application.dto.CouponResponseDto;
import com.taken_seat.coupon_service.domain.entity.Coupon;
import com.taken_seat.coupon_service.domain.repository.CouponRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CouponService {

    private final CouponRepository couponRepository;

    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    @Transactional
    public CouponResponseDto createCoupon(CouponDto dto) {
        Coupon coupon = Coupon.create(
                dto.getName(), dto.getCode(), dto.getQuantity(),
                dto.getDiscount(), dto.getExpiredAt()
        );

        couponRepository.save(coupon);

        return CouponResponseDto.of(coupon);
    }
}
