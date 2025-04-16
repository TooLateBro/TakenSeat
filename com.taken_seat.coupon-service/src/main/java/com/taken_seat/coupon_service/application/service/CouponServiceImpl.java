package com.taken_seat.coupon_service.application.service;

import com.taken_seat.common_service.dto.AuthenticatedUser;
import com.taken_seat.coupon_service.application.dto.CouponDto;
import com.taken_seat.coupon_service.application.dto.CouponResponseDto;
import com.taken_seat.coupon_service.application.dto.CouponUpdateDto;
import com.taken_seat.coupon_service.application.dto.PageResponseDto;
import com.taken_seat.coupon_service.domain.entity.Coupon;
import com.taken_seat.coupon_service.domain.repository.CouponQueryRepository;
import com.taken_seat.coupon_service.domain.repository.CouponRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private final CouponQueryRepository couponQueryRepository;

    public CouponServiceImpl(CouponRepository couponRepository, CouponQueryRepository couponQueryRepository) {
        this.couponRepository = couponRepository;
        this.couponQueryRepository = couponQueryRepository;
    }

    @Transactional
    @Override
    @CachePut(cacheNames = "couponCache", key = "#result.id")
    public CouponResponseDto createCoupon(CouponDto dto, AuthenticatedUser authenticatedUser) {
        Coupon coupon = Coupon.create(
                dto.getName(), dto.getCode(), dto.getQuantity(),
                dto.getDiscount(), dto.getExpiredAt(), authenticatedUser.getUserId()
        );

        couponRepository.save(coupon);

        return CouponResponseDto.of(coupon);
    }

    @Transactional(readOnly = true)
    @Override
    public CouponResponseDto getCoupon(UUID couponId) {
        Coupon coupon = couponRepository.findByIdAndDeletedAtIsNull(couponId)
                .orElseThrow(()-> new IllegalArgumentException("쿠폰이 존재하지 않습니다."));

        return CouponResponseDto.of(coupon);
    }

    @Transactional(readOnly = true)
    @Override
    @Cacheable(cacheNames = "searchCache", key = "#name +'-'+ #page + '-' + #size")
    public PageResponseDto<CouponResponseDto> searchCoupon(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Coupon> coupons = couponQueryRepository.findAllByDeletedAtIsNull(name, pageable);

        Page<CouponResponseDto> couponsInfo = coupons.map(CouponResponseDto::of);

        return PageResponseDto.of(couponsInfo);
    }

    @Transactional
    @Override
    @CachePut(cacheNames = "couponCache", key = "#result.id")
    @Caching(evict = {
            @CacheEvict(cacheNames = "searchCache", allEntries = true)
    })
    public CouponResponseDto updateCoupon(UUID couponId, AuthenticatedUser authenticatedUser, CouponUpdateDto dto) {
        Coupon coupon = couponRepository.findByIdAndDeletedAtIsNull(couponId)
                .orElseThrow(()-> new IllegalArgumentException("쿠폰이 존재하지 않습니다."));

        coupon.update(
                dto.getName().orElse(coupon.getName()), dto.getCode().orElse(coupon.getCode()),
                dto.getQuantity().orElse(coupon.getQuantity()), dto.getDiscount().orElse(coupon.getDiscount()),
                dto.getExpiredAt().orElse(coupon.getExpiredAt()), authenticatedUser.getUserId()
        );
        return CouponResponseDto.of(coupon);
    }

    @Transactional
    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "couponCache", allEntries = true),
            @CacheEvict(cacheNames = "searchCache", allEntries = true)
    })
    public void deleteCoupon(UUID couponId, AuthenticatedUser authenticatedUser) {
        Coupon coupon = couponRepository.findByIdAndDeletedAtIsNull(couponId)
                .orElseThrow(()-> new IllegalArgumentException("쿠폰이 존재하지 않습니다."));

        coupon.delete(authenticatedUser.getUserId());
    }
}
