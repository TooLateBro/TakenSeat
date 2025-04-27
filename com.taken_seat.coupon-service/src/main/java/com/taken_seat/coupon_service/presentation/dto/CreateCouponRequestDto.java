package com.taken_seat.coupon_service.presentation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.taken_seat.coupon_service.application.dto.CouponDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record CreateCouponRequestDto(
        @Size(min = 1, max = 100, message = "쿠폰 이름은 1자 이상 100자 이하로 입력해주세요.")
        @Schema(name = "CouponName")
        String name,

        @Size(min = 1, max = 50, message = "코드는 1자 이상 50자 이하로 입력해주세요.")
        @Schema(name = "CouponCode")
        String code,

        @Min(value = 1, message = "수량은 1 이상이어야 합니다.")
        @Schema(name = "1")
        Long quantity,

        @Min(value = 1, message = "할인율은 1 이상이어야 합니다.")
        @Schema(name = "1")
        Integer discount,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @Schema(name = "2025-12-31 23:59:59")
        LocalDateTime expiredAt
) {
    public CouponDto toDto(){
        return CouponDto.create(this.name, this.code, this.quantity, this.discount, this.expiredAt);
    }
}
