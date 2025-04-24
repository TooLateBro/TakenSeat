package com.taken_seat.coupon_service.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record CouponUpdateDto(
        String name,
        String code,
        Long quantity,
        Integer discount,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime expiredAt
) {
    public static CouponUpdateDto update(String name, String code, Long quantity, Integer discount, LocalDateTime expiredAt) {
        return new CouponUpdateDto(name, code, quantity, discount, expiredAt);
    }
}
