package com.taken_seat.coupon_service.presentation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.taken_seat.coupon_service.application.dto.CouponDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateCouponRequestDto {

    
    private String name;

    private String code;

    private Long quantity;

    private Integer discount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
    private LocalDateTime expiredAt;

    public CouponDto toDto(){
        return CouponDto.create(this.name, this.code, this.quantity, this.discount, this.expiredAt);
    }
}
