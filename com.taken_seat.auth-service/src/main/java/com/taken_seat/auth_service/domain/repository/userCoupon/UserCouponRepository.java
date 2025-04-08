package com.taken_seat.auth_service.domain.repository.userCoupon;

import com.taken_seat.auth_service.domain.entity.user.UserCoupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserCouponRepository {

    Page<UserCoupon> findByUserIdAndIsActiveFalse(UUID id, Pageable pageable);
}
