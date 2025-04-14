package com.taken_seat.coupon_service.application.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Builder(access = AccessLevel.PRIVATE)
public class CouponDto {

    private String name;

    private String code;

    private Long quantity;

    private Integer discount;

    private LocalDateTime expiredAt;

    public static CouponDto create(String name, String code, Long quantity, Integer discount, LocalDateTime expiredAt) {
        return CouponDto.builder()
                .name(name)
                .code(code)
                .quantity(quantity)
                .discount(discount)
                .expiredAt(expiredAt)
                .build();
    }

}
