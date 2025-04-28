package com.taken_seat.coupon_service.application.dto;

import com.taken_seat.coupon_service.domain.entity.Coupon;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface CouponMapper {

    CouponResponseDto couponToCouponResponseDto(Coupon coupon);
}
