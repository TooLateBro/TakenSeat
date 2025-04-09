package com.taken_seat.coupon_service.application.service;

import com.taken_seat.coupon_service.application.dto.CouponDto;
import com.taken_seat.coupon_service.application.dto.CouponResponseDto;
import com.taken_seat.coupon_service.application.dto.PageResponseDto;
import com.taken_seat.coupon_service.domain.entity.Coupon;
import com.taken_seat.coupon_service.domain.repository.CouponQueryRepository;
import com.taken_seat.coupon_service.domain.repository.CouponRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponQueryRepository couponQueryRepository;

    public CouponService(CouponRepository couponRepository, CouponQueryRepository couponQueryRepository) {
        this.couponRepository = couponRepository;
        this.couponQueryRepository = couponQueryRepository;
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

    @Transactional(readOnly = true)
    public CouponResponseDto getCoupon(UUID couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(()-> new IllegalArgumentException("쿠폰이 존재하지 않습니다."));

        return CouponResponseDto.of(coupon);
    }

    @Transactional(readOnly = true)
    public PageResponseDto<CouponResponseDto> searchCoupon(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Coupon> coupons = couponQueryRepository.findAllByDeletedAtIsNull(name, pageable);

        Page<CouponResponseDto> couponsInfo = coupons.map(CouponResponseDto::of);

        return PageResponseDto.of(couponsInfo);
    }
}
