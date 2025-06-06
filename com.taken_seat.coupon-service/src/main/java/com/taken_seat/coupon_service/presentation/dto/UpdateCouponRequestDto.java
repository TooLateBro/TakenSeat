package com.taken_seat.coupon_service.presentation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.taken_seat.coupon_service.application.dto.CouponDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateCouponRequestDto {

    @Size(min = 1, max = 100, message = "쿠폰 이름은 1자 이상 100자 이하로 입력해주세요.")
    @Schema(example = "CouponName")
    private String name;

    @Size(min = 1, max = 50, message = "코드는 1자 이상 50자 이하로 입력해주세요.")
    @Schema(example = "CouponCode")
    private String code;

    @Min(value = 1, message = "수량은 1 이상이어야 합니다.")
    @Schema(example = "1")
    private Long quantity;

    @Min(value = 1, message = "할인율은 1 이상이어야 합니다.")
    @Schema(example = "1")
    private Integer discount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(example = "2025-12-31 23:59:59")
    private LocalDateTime expiredAt;

    public CouponDto toDto(){
        return CouponDto.update(this.name, this.code, this.quantity, this.discount, this.expiredAt);
    }
}
