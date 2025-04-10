package com.taken_seat.coupon_service.presentation.controller;

import com.taken_seat.common_service.dto.ApiResponseData;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import com.taken_seat.coupon_service.application.dto.CouponResponseDto;
import com.taken_seat.coupon_service.application.dto.PageResponseDto;
import com.taken_seat.coupon_service.application.service.CouponService;
import com.taken_seat.coupon_service.presentation.dto.CreateCouponRequestDto;
import com.taken_seat.coupon_service.presentation.dto.UpdateCouponRequestDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/coupons")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @PostMapping
    public ResponseEntity<ApiResponseData<CouponResponseDto>> createCoupon(
            @RequestHeader("X-Role") String role,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestBody CreateCouponRequestDto requestDto){
        if (role == null || !(role.equals("ADMIN") || role.equals("MANAGER"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponseData.failure(ResponseCode.ACCESS_DENIED_EXCEPTION.getCode()
                            ,"접근 권한이 없습니다."));
        }
        CouponResponseDto couponInfo = couponService.createCoupon(requestDto.toDto(), userId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseData.success(couponInfo));
    }

    @GetMapping("/{couponId}")
    public ResponseEntity<ApiResponseData<CouponResponseDto>> getCoupon(@RequestHeader("X-Role") String role,
                                                                        @PathVariable UUID couponId) {
        if (role == null || !(role.equals("ADMIN") || role.equals("MANAGER"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponseData.failure(ResponseCode.ACCESS_DENIED_EXCEPTION.getCode()
                            ,"접근 권한이 없습니다."));
        }
        CouponResponseDto couponInfo = couponService.getCoupon(couponId);
        return ResponseEntity.ok(ApiResponseData.success(couponInfo));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponseData<PageResponseDto<CouponResponseDto>>> searchCoupon(
            @RequestHeader("X-Role") String role,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        if (role == null || !(role.equals("ADMIN") || role.equals("MANAGER"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponseData.failure(ResponseCode.ACCESS_DENIED_EXCEPTION.getCode()
                            ,"접근 권한이 없습니다."));
        }
        PageResponseDto<CouponResponseDto> couponInfo = couponService.searchCoupon(name, page, size);
        return ResponseEntity.ok(ApiResponseData.success(couponInfo));
    }

    @PatchMapping("/{couponId}")
    public ResponseEntity<ApiResponseData<CouponResponseDto>> updateCoupon(@PathVariable UUID couponId,
                                                                           @RequestHeader("X-Role") String role,
                                                                           @RequestHeader("X-User-Id") UUID userId,
                                                                           @RequestBody UpdateCouponRequestDto requestDto){
        if (role == null || !(role.equals("ADMIN") || role.equals("MANAGER"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponseData.failure(ResponseCode.ACCESS_DENIED_EXCEPTION.getCode()
                            ,"접근 권한이 없습니다."));
        }
        CouponResponseDto couponInfo = couponService.updateCoupon(couponId, userId, requestDto.toDto());
        return ResponseEntity.ok(ApiResponseData.success(couponInfo));
    }

    @DeleteMapping("/{couponId}")
    public ResponseEntity<ApiResponseData<Void>> deleteCoupon(@PathVariable UUID couponId,
                                                              @RequestHeader("X-Role") String role,
                                                              @RequestHeader("X-User-Id") UUID userId) {
        if (role == null || !(role.equals("ADMIN") || role.equals("MANAGER"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponseData.failure(ResponseCode.ACCESS_DENIED_EXCEPTION.getCode()
                            ,"접근 권한이 없습니다."));
        }
        couponService.deleteCoupon(couponId, userId);
        return ResponseEntity.ok(ApiResponseData.success(null));
    }
}
