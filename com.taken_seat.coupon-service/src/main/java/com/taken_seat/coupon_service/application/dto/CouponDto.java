package com.taken_seat.coupon_service.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record CouponDto(
        String name,
        String code,
        Long quantity,
        Integer discount,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime expiredAt
) {
    public static CouponDto create(String name, String code, Long quantity, Integer discount, LocalDateTime expiredAt) {
        return new CouponDto(name, code, quantity, discount, expiredAt);
    }
    public static CouponDto update(String name, String code, Long quantity, Integer discount, LocalDateTime expiredAt) {
        return new CouponDto(name, code, quantity, discount, expiredAt);
    }
}
