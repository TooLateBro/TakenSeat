package com.taken_seat.coupon_service.presentation.controller;

import com.taken_seat.common_service.dto.ApiResponseData;
import com.taken_seat.common_service.dto.AuthenticatedUser;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import com.taken_seat.coupon_service.application.dto.CouponResponseDto;
import com.taken_seat.coupon_service.application.dto.PageResponseDto;
import com.taken_seat.coupon_service.application.service.CouponService;
import com.taken_seat.coupon_service.presentation.docs.CouponControllerDocs;
import com.taken_seat.coupon_service.presentation.dto.CreateCouponRequestDto;
import com.taken_seat.coupon_service.presentation.dto.UpdateCouponRequestDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/coupons")
public class CouponController implements CouponControllerDocs {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @PostMapping
    public ResponseEntity<ApiResponseData<CouponResponseDto>> createCoupon(AuthenticatedUser authenticatedUser,
                                                                           @RequestBody CreateCouponRequestDto requestDto){
        if (authenticatedUser.getRole() == null ||
                !(authenticatedUser.getRole().equals("ADMIN") ||
                        authenticatedUser.getRole().equals("MANAGER"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponseData.failure(ResponseCode.ACCESS_DENIED_EXCEPTION.getCode()
                            ,"접근 권한이 없습니다."));
        }
        CouponResponseDto couponInfo = couponService.createCoupon(requestDto.toDto(), authenticatedUser);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseData.success(couponInfo));
    }

    @GetMapping("/{couponId}")
    public ResponseEntity<ApiResponseData<CouponResponseDto>> getCoupon(AuthenticatedUser authenticatedUser,
                                                                        @PathVariable UUID couponId) {
        if (authenticatedUser.getRole() == null ||
                !(authenticatedUser.getRole().equals("ADMIN") ||
                        authenticatedUser.getRole().equals("MANAGER"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponseData.failure(ResponseCode.ACCESS_DENIED_EXCEPTION.getCode()
                            ,"접근 권한이 없습니다."));
        }
        CouponResponseDto couponInfo = couponService.getCoupon(couponId);
        return ResponseEntity.ok(ApiResponseData.success(couponInfo));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponseData<PageResponseDto<CouponResponseDto>>> searchCoupon(AuthenticatedUser authenticatedUser,
                                                                                            @RequestParam(required = false) String name,
                                                                                            @RequestParam(defaultValue = "0") int page,
                                                                                            @RequestParam(defaultValue = "10") int size
    ){
        if (authenticatedUser.getRole() == null ||
                !(authenticatedUser.getRole().equals("ADMIN") ||
                        authenticatedUser.getRole().equals("MANAGER"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponseData.failure(ResponseCode.ACCESS_DENIED_EXCEPTION.getCode()
                            ,"접근 권한이 없습니다."));
        }
        PageResponseDto<CouponResponseDto> couponInfo = couponService.searchCoupon(name, page, size);
        return ResponseEntity.ok(ApiResponseData.success(couponInfo));
    }

    @PatchMapping("/{couponId}")
    public ResponseEntity<ApiResponseData<CouponResponseDto>> updateCoupon(@PathVariable UUID couponId,
                                                                           AuthenticatedUser authenticatedUser,
                                                                           @RequestBody UpdateCouponRequestDto requestDto){
        if (authenticatedUser.getRole() == null ||
                !(authenticatedUser.getRole().equals("ADMIN") ||
                        authenticatedUser.getRole().equals("MANAGER"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponseData.failure(ResponseCode.ACCESS_DENIED_EXCEPTION.getCode()
                            ,"접근 권한이 없습니다."));
        }
        CouponResponseDto couponInfo = couponService.updateCoupon(couponId, authenticatedUser, requestDto.toDto());
        return ResponseEntity.ok(ApiResponseData.success(couponInfo));
    }

    @DeleteMapping("/{couponId}")
    public ResponseEntity<ApiResponseData<Void>> deleteCoupon(@PathVariable UUID couponId,
                                                              AuthenticatedUser authenticatedUser) {
        if (authenticatedUser.getRole() == null ||
                !(authenticatedUser.getRole().equals("ADMIN") ||
                        authenticatedUser.getRole().equals("MANAGER"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponseData.failure(ResponseCode.ACCESS_DENIED_EXCEPTION.getCode()
                            ,"접근 권한이 없습니다."));
        }
        couponService.deleteCoupon(couponId, authenticatedUser);
        return ResponseEntity.ok(ApiResponseData.success(null));
    }
}
