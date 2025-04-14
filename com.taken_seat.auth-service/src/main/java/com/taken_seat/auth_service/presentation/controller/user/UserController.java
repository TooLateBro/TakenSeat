package com.taken_seat.auth_service.presentation.controller.user;

import com.taken_seat.auth_service.application.dto.PageResponseDto;
import com.taken_seat.auth_service.application.dto.user.UserInfoResponseDto;
import com.taken_seat.auth_service.application.service.user.UserService;
import com.taken_seat.auth_service.presentation.dto.user.UserUpdateRequestDto;
import com.taken_seat.common_service.dto.ApiResponseData;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponseData<UserInfoResponseDto>> getUser(@PathVariable UUID userId) {
        UserInfoResponseDto userInfo = userService.getUser(userId);
        return ResponseEntity.ok(ApiResponseData.success(userInfo));
    }

    // 쿠폰 발급에 성공한 쿠폰인지 아닌지 확인하는 API
    @GetMapping("/status/{couponId}")
    public ResponseEntity<ApiResponseData<String>> getCoupon(@PathVariable UUID couponId) {
        String result = userService.getCoupon(couponId);
        return ResponseEntity.ok(ApiResponseData.success(result));
    }

    @GetMapping("/details/{userId}")
    public ResponseEntity<ApiResponseData<UserInfoResponseDto>> getUserDetails(@PathVariable UUID userId,
                                                                               @RequestParam(defaultValue = "0") int page,
                                                                               @RequestParam(defaultValue = "10") int size) {
        UserInfoResponseDto userInfo = userService.getUserDetails(userId, page, size);
        return ResponseEntity.ok(ApiResponseData.success(userInfo));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponseData<PageResponseDto<UserInfoResponseDto>>> searchUser(@RequestHeader("X-Role") String userRole,
                                                                                            @RequestParam(required = false) String q,
                                                                                            @RequestParam(required = false) String role,
                                                                                            @RequestParam(defaultValue = "0") int page,
                                                                                            @RequestParam(defaultValue = "10") int size) {

        if (userRole == null || !(userRole.equals("ADMIN") || userRole.equals("MANAGER"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponseData.failure(ResponseCode.ACCESS_DENIED_EXCEPTION.getCode()
                            ,"접근 권한이 없습니다."));
        }

        PageResponseDto<UserInfoResponseDto> userInfo = userService.searchUser(q, role, page, size);
        return ResponseEntity.ok(ApiResponseData.success(userInfo));
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<ApiResponseData<UserInfoResponseDto>> updateUser(@PathVariable UUID userId,
                                                                           @Valid @RequestBody UserUpdateRequestDto requestDto) {

        UserInfoResponseDto userInfo = userService.updateUser(userId, requestDto.toDto());
        return ResponseEntity.ok(ApiResponseData.success(userInfo));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponseData<Void>> deleteUser(@PathVariable UUID userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok(ApiResponseData.success(null));
    }
}
