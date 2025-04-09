package com.taken_seat.coupon_service.presentation.controller;

import com.taken_seat.coupon_service.application.dto.CouponResponseDto;
import com.taken_seat.coupon_service.application.service.CouponService;
import com.taken_seat.coupon_service.presentation.dto.CreateCouponRequestDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/coupons")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @PostMapping
    public ResponseEntity<CouponResponseDto> createCoupon(
            @RequestHeader("X-Role") String role,
            @RequestBody CreateCouponRequestDto requestDto){
        if(role == null || !(role.equals("ADMIN") || role.equals("MANAGER"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        CouponResponseDto couponInfo = couponService.createCoupon(requestDto.toDto());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(couponInfo);
    }
}
