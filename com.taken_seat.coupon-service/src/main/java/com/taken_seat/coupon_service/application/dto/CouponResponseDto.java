package com.taken_seat.coupon_service.application.dto;

import com.taken_seat.coupon_service.domain.entity.Coupon;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class CouponResponseDto {

    private UUID id;
    private String name;
    private String code;
    private Integer quantity;
    private Integer discount;
    private LocalDateTime expiredAt;

    public static CouponResponseDto of(Coupon coupon) {
        return CouponResponseDto.builder()
                .id(coupon.getId())
                .name(coupon.getName())
                .code(coupon.getCode())
                .quantity(coupon.getQuantity())
                .discount(coupon.getDiscount())
                .expiredAt(coupon.getExpiredAt())
                .build();
    }
}
