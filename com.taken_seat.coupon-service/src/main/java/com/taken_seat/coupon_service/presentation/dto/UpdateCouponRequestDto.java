package com.taken_seat.coupon_service.presentation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.taken_seat.coupon_service.application.dto.CouponUpdateDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Optional;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateCouponRequestDto {

    private Optional<String> name;

    private Optional<String> code;

    private Optional<Integer> quantity;

    private Optional<Integer> discount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
    private Optional<LocalDateTime> expiredAt;

    public CouponUpdateDto toDto(){
        return CouponUpdateDto.update(this.name, this.code, this.quantity, this.discount, this.expiredAt);
    }
}
