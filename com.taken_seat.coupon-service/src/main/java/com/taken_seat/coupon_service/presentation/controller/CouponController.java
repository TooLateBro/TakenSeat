package com.taken_seat.coupon_service.presentation.controller;

import com.taken_seat.common_service.dto.ApiResponseData;
import com.taken_seat.common_service.dto.AuthenticatedUser;
import com.taken_seat.coupon_service.application.dto.CouponResponseDto;
import com.taken_seat.coupon_service.application.dto.PageResponseDto;
import com.taken_seat.coupon_service.application.service.CouponService;
import com.taken_seat.coupon_service.domain.entity.vo.Role;
import com.taken_seat.coupon_service.infrastructure.role.RoleCheck;
import com.taken_seat.coupon_service.presentation.docs.CouponControllerDocs;
import com.taken_seat.coupon_service.presentation.dto.CreateCouponRequestDto;
import com.taken_seat.coupon_service.presentation.dto.UpdateCouponRequestDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/coupons")
@RoleCheck(allowedRoles = {Role.ADMIN, Role.MANAGER})
public class CouponController implements CouponControllerDocs {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @PostMapping
    public ResponseEntity<ApiResponseData<CouponResponseDto>> createCoupon(AuthenticatedUser authenticatedUser,
                                                                           @Valid @RequestBody CreateCouponRequestDto requestDto){

        CouponResponseDto couponInfo = couponService.createCoupon(requestDto.toDto(), authenticatedUser);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseData.success(couponInfo));
    }

    @GetMapping("/{couponId}")
    public ResponseEntity<ApiResponseData<CouponResponseDto>> getCoupon(AuthenticatedUser authenticatedUser,
                                                                        @PathVariable UUID couponId) {

        CouponResponseDto couponInfo = couponService.getCoupon(couponId);
        return ResponseEntity.ok(ApiResponseData.success(couponInfo));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponseData<PageResponseDto<CouponResponseDto>>> searchCoupon(AuthenticatedUser authenticatedUser,
                                                                                            @RequestParam(required = false) String name,
                                                                                            @RequestParam(defaultValue = "0") int page,
                                                                                            @RequestParam(defaultValue = "10") int size
    ){
        PageResponseDto<CouponResponseDto> couponInfo = couponService.searchCoupon(name, page, size);
        return ResponseEntity.ok(ApiResponseData.success(couponInfo));
    }

    @PatchMapping("/{couponId}")
    public ResponseEntity<ApiResponseData<CouponResponseDto>> updateCoupon(@PathVariable UUID couponId,
                                                                           AuthenticatedUser authenticatedUser,
                                                                           @Valid @RequestBody UpdateCouponRequestDto requestDto){

        CouponResponseDto couponInfo = couponService.updateCoupon(couponId, authenticatedUser, requestDto.toDto());
        return ResponseEntity.ok(ApiResponseData.success(couponInfo));
    }

    @DeleteMapping("/{couponId}")
    public ResponseEntity<ApiResponseData<Void>> deleteCoupon(@PathVariable UUID couponId,
                                                              AuthenticatedUser authenticatedUser) {

        couponService.deleteCoupon(couponId, authenticatedUser);
        return ResponseEntity.ok(ApiResponseData.success(null));
    }
}
