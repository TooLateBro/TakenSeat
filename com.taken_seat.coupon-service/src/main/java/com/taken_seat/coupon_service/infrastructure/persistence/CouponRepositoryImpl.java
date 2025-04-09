package com.taken_seat.coupon_service.infrastructure.persistence;

import com.taken_seat.coupon_service.domain.entity.Coupon;
import com.taken_seat.coupon_service.domain.repository.CouponRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CouponRepositoryImpl extends JpaRepository<Coupon, UUID>, CouponRepository {
}
