package com.taken_seat.coupon_service.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.taken_seat.coupon_service.domain.entity.Coupon;

import java.time.LocalDateTime;
import java.util.UUID;

public record CouponResponseDto(
        UUID id,
        String name,
        String code,
        Long quantity,
        Integer discount,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime expiredAt
) {}
