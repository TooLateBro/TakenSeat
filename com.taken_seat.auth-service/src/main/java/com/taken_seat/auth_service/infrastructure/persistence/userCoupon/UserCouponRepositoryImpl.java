package com.taken_seat.auth_service.infrastructure.persistence.userCoupon;

import com.taken_seat.auth_service.domain.entity.user.UserCoupon;
import com.taken_seat.auth_service.domain.repository.userCoupon.UserCouponRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserCouponRepositoryImpl extends JpaRepository<UserCoupon, UUID> , UserCouponRepository {
}
