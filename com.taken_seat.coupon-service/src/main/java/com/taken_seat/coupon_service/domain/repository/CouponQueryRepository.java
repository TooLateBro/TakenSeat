package com.taken_seat.coupon_service.domain.repository;

import com.taken_seat.coupon_service.domain.entity.Coupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CouponQueryRepository {
    Page<Coupon> findAllByDeletedAtIsNull(String name, Pageable pageable);
}
