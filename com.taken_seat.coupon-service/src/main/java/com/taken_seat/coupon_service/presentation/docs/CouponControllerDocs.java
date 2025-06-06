package com.taken_seat.coupon_service.presentation.docs;

import com.taken_seat.common_service.dto.ApiResponseData;
import com.taken_seat.common_service.dto.AuthenticatedUser;
import com.taken_seat.coupon_service.application.dto.CouponResponseDto;
import com.taken_seat.coupon_service.application.dto.PageResponseDto;
import com.taken_seat.coupon_service.presentation.dto.CreateCouponRequestDto;
import com.taken_seat.coupon_service.presentation.dto.UpdateCouponRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "쿠폰", description = "쿠폰 생성, 조회, 수정, 삭제를 담당하는 API 입니다.")
public interface CouponControllerDocs {

    @PostMapping("/api/v1/coupons")
    @Operation(summary = "쿠폰 생성", description = "쿠폰을 생성하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "쿠폰 생성 성공",
                    content = @Content(schema = @Schema(implementation = CouponResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "유효하지 않은 데이터",
                    content = @Content(schema = @Schema(implementation = ApiResponseData.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "이미 존재하는 쿠폰입니다.",
                    content = @Content(schema = @Schema(implementation = ApiResponseData.class))
            )
    })
    ResponseEntity<ApiResponseData<CouponResponseDto>> createCoupon(AuthenticatedUser authenticatedUser,
                                                                           @RequestBody CreateCouponRequestDto requestDto);

    @GetMapping("/api/v1/coupons/{couponId}")
    @Operation(summary = "쿠폰 단건 조회", description = "쿠폰을 단건 조회하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "쿠폰 단건 조회 성공",
                    content = @Content(schema = @Schema(implementation = CouponResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "유효하지 않은 데이터",
                    content = @Content(schema = @Schema(implementation = ApiResponseData.class))
            )
    })
    ResponseEntity<ApiResponseData<CouponResponseDto>> getCoupon(AuthenticatedUser authenticatedUser,
                                                                        @PathVariable UUID couponId);

    @GetMapping("/api/v1/coupons/search")
    @Operation(summary = "쿠폰 전체 조회", description = "쿠폰을 전체 조회 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "쿠폰 전체 조회 성공",
                    content = @Content(schema = @Schema(implementation = CouponResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "유효하지 않은 데이터",
                    content = @Content(schema = @Schema(implementation = ApiResponseData.class))
            )
    })
    ResponseEntity<ApiResponseData<PageResponseDto<CouponResponseDto>>> searchCoupon(AuthenticatedUser authenticatedUser,
                                                                                            @RequestParam(required = false) String name,
                                                                                            @RequestParam(defaultValue = "0") int page,
                                                                                            @RequestParam(defaultValue = "10") int size
    );

    @PatchMapping("/api/v1/coupons/{couponId}")
    @Operation(summary = "쿠폰 수정", description = "쿠폰을 수정하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "쿠폰 수정 성공",
                    content = @Content(schema = @Schema(implementation = CouponResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "유효하지 않은 데이터",
                    content = @Content(schema = @Schema(implementation = ApiResponseData.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "쿠폰이 존재하지 않습니다.",
                    content = @Content(schema = @Schema(implementation = ApiResponseData.class))
            )
    })
    ResponseEntity<ApiResponseData<CouponResponseDto>> updateCoupon(@PathVariable UUID couponId,
                                                                           AuthenticatedUser authenticatedUser,
                                                                           @RequestBody UpdateCouponRequestDto requestDto);

    @DeleteMapping("/api/v1/coupons/{couponId}")
    @Operation(summary = "쿠폰 삭제", description = "쿠폰을 삭제하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "쿠폰 삭제 성공",
                    content = @Content(schema = @Schema(implementation = CouponResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "유효하지 않은 데이터",
                    content = @Content(schema = @Schema(implementation = ApiResponseData.class))
            )
    })
    ResponseEntity<ApiResponseData<Void>> deleteCoupon(@PathVariable UUID couponId,
                                                              AuthenticatedUser authenticatedUser);
}
