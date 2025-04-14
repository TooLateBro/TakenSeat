package com.taken_seat.auth_service.domain.repository.userCoupon;

import com.taken_seat.auth_service.domain.entity.user.UserCoupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserCouponRepository {

    UserCoupon save(UserCoupon userCoupon);

    Optional<UserCoupon> findByUserIdAndCouponIdAndDeletedAtIsNull(UUID userId, UUID couponId);

    Page<UserCoupon> findCouponIdByUserIdAndIsActiveTrue(UUID id, Pageable pageable);
}
