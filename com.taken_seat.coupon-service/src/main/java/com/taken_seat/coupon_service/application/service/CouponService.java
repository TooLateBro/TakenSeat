package com.taken_seat.coupon_service.application.service;

import com.taken_seat.common_service.dto.AuthenticatedUser;
import com.taken_seat.coupon_service.application.dto.CouponDto;
import com.taken_seat.coupon_service.application.dto.CouponResponseDto;
import com.taken_seat.coupon_service.application.dto.CouponUpdateDto;
import com.taken_seat.coupon_service.application.dto.PageResponseDto;

import java.util.UUID;

public interface CouponService {

    CouponResponseDto createCoupon(CouponDto dto, AuthenticatedUser authenticatedUser);

    CouponResponseDto getCoupon(UUID couponId);

    PageResponseDto<CouponResponseDto> searchCoupon(String name, int page, int size);

    CouponResponseDto updateCoupon(UUID couponId, AuthenticatedUser authenticatedUser, CouponUpdateDto dto);

    void deleteCoupon(UUID couponId, AuthenticatedUser authenticatedUser);
}
