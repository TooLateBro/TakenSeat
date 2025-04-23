package com.taken_seat.coupon_service.application.dto;

import java.time.LocalDateTime;

public record CouponDto(
        String name,
        String code,
        Long quantity,
        Integer discount,
        LocalDateTime expiredAt
) {
    public static CouponDto create(String name, String code, Long quantity, Integer discount, LocalDateTime expiredAt) {
        return new CouponDto(name, code, quantity, discount, expiredAt);
    }

}
