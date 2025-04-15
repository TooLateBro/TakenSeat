package com.taken_seat.auth_service.presentation.docs;

import com.taken_seat.auth_service.application.dto.PageResponseDto;
import com.taken_seat.auth_service.application.dto.user.UserInfoResponseDto;
import com.taken_seat.auth_service.presentation.dto.user.UserUpdateRequestDto;
import com.taken_seat.common_service.dto.ApiResponseData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "유저", description = "검색, 수정, 삭제를 담당하는 API 입니다.")
public interface UserControllerDocs {

    @GetMapping("/api/v1/users/{userId}")
    @Operation(summary = "유저 단건 조회", description = "유저 정보만 단건 조회하는 API 입니다.")
    ResponseEntity<ApiResponseData<UserInfoResponseDto>> getUser(@PathVariable UUID userId);

    // 쿠폰 발급에 성공한 쿠폰인지 아닌지 확인하는 API
    @GetMapping("/api/v1/users/status/{couponId}")
    @Operation(summary = "유저 발급 쿠폰", description = "유저 발급 요청한 쿠폰의 성공 유무를 조회하는 API 입니다.")
    ResponseEntity<ApiResponseData<String>> getCoupon(@PathVariable UUID couponId);

    @GetMapping("/api/v1/users/details/{userId}")
    @Operation(summary = "유저 단건 상세 조회", description = "유저 단건 정보와 유저가 보유한 마일리지 및 쿠폰을 조회하는 API 입니다.")
    ResponseEntity<ApiResponseData<UserInfoResponseDto>> getUserDetails(@PathVariable UUID userId,
                                                                               @RequestParam(defaultValue = "0") int page,
                                                                               @RequestParam(defaultValue = "10") int size);

    @GetMapping("/api/v1/users/search")
    @Operation(summary = "유저 전체 조회", description = "전체 유저를 조회하는 API 입니다.")
    ResponseEntity<ApiResponseData<PageResponseDto<UserInfoResponseDto>>> searchUser(@RequestHeader("X-Role") String userRole,
                                                                                            @RequestParam(required = false) String q,
                                                                                            @RequestParam(required = false) String role,
                                                                                            @RequestParam(defaultValue = "0") int page,
                                                                                            @RequestParam(defaultValue = "10") int size);

    @PatchMapping("/api/v1/users/{userId}")
    @Operation(summary = "유저 정보 수정", description = "유저 정보를 수정하는 API 입니다.")
    ResponseEntity<ApiResponseData<UserInfoResponseDto>> updateUser(@PathVariable UUID userId,
                                                                           @Valid @RequestBody UserUpdateRequestDto requestDto);

    @DeleteMapping("/api/v1/users/{userId}")
    @Operation(summary = "유저 삭제", description = "유저를 삭제 하는 API 입니다.")
    ResponseEntity<ApiResponseData<Void>> deleteUser(@PathVariable UUID userId);
}
