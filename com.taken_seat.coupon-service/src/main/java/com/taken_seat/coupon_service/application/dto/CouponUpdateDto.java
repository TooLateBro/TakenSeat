package com.taken_seat.coupon_service.application.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Optional;

@AllArgsConstructor
@Getter
@Builder(access = AccessLevel.PRIVATE)
public class CouponUpdateDto {

    private Optional<String> name;

    private Optional<String> code;

    private Optional<Integer> quantity;

    private Optional<Integer> discount;

    private Optional<LocalDateTime> expiredAt;

    public static CouponUpdateDto update(Optional<String> name, Optional<String> code,
                                         Optional<Integer> quantity, Optional<Integer> discount,
                                         Optional<LocalDateTime> expiredAt) {
        return CouponUpdateDto.builder()
                .name(name)
                .code(code)
                .quantity(quantity)
                .discount(discount)
                .expiredAt(expiredAt)
                .build();
    }
}
