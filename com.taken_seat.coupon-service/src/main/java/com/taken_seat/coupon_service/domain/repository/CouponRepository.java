package com.taken_seat.coupon_service.domain.repository;

import com.taken_seat.coupon_service.domain.entity.Coupon;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CouponRepository {

    Coupon save(Coupon coupon);

    Optional<Coupon> findByIdAndDeletedAtIsNull(UUID couponId);

    Optional<Coupon> findByCode(String code);
}
