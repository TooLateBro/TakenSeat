package com.taken_seat.auth_service.presentation.controller.user.v1;

import com.taken_seat.auth_service.application.dto.PageResponseDto;
import com.taken_seat.auth_service.application.dto.user.v1.UserDetailsResponseDtoV1;
import com.taken_seat.auth_service.application.dto.user.v1.UserInfoResponseDtoV1;
import com.taken_seat.auth_service.application.dto.user.v1.UserMapper;
import com.taken_seat.auth_service.application.service.user.v1.UserServiceV1;
import com.taken_seat.auth_service.presentation.docs.UserControllerDocs;
import com.taken_seat.auth_service.presentation.dto.user.UserUpdateRequestDto;
import com.taken_seat.common_service.aop.annotation.RoleCheck;
import com.taken_seat.common_service.aop.vo.Role;
import com.taken_seat.common_service.dto.ApiResponseData;
import com.taken_seat.common_service.dto.AuthenticatedUser;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RoleCheck()
public class UserControllerV1 implements UserControllerDocs {

    private final UserServiceV1 userServiceV1;
    private final UserMapper userMapper;

    public UserControllerV1(UserServiceV1 userServiceV1, UserMapper userMapper) {
        this.userServiceV1 = userServiceV1;
        this.userMapper = userMapper;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponseData<UserInfoResponseDtoV1>> getUser(@PathVariable UUID userId,
                                                                          AuthenticatedUser authenticatedUser) {

        UserInfoResponseDtoV1 userInfo = userServiceV1.getUser(userId);
        return ResponseEntity.ok(ApiResponseData.success(userInfo));
    }

    // 쿠폰 발급에 성공한 쿠폰인지 아닌지 확인하는 API
    @GetMapping("/status/{couponId}")
    public ResponseEntity<ApiResponseData<String>> getCoupon(@PathVariable UUID couponId,
                                                             AuthenticatedUser authenticatedUser) {

        String result = userServiceV1.getCoupon(couponId);
        return ResponseEntity.ok(ApiResponseData.success(result));
    }

    @GetMapping("/details/{userId}")
    public ResponseEntity<ApiResponseData<UserDetailsResponseDtoV1>> getUserDetails(@PathVariable UUID userId,
                                                                                    @RequestParam(defaultValue = "0") int page,
                                                                                    @RequestParam(defaultValue = "10") int size,
                                                                                    AuthenticatedUser authenticatedUser) {

        UserDetailsResponseDtoV1 userInfo = userServiceV1.getUserDetails(userId, page, size);
        return ResponseEntity.ok(ApiResponseData.success(userInfo));
    }

    @GetMapping("/search")
    @RoleCheck(allowedRoles = {Role.ADMIN, Role.MANAGER})
    public ResponseEntity<ApiResponseData<PageResponseDto<UserDetailsResponseDtoV1>>> searchUser(@RequestParam(required = false) String username,
                                                                                              @RequestParam(required = false) String role,
                                                                                              @RequestParam(defaultValue = "0") int page,
                                                                                              @RequestParam(defaultValue = "10") int size,
                                                                                              AuthenticatedUser authenticatedUser) {

        PageResponseDto<UserDetailsResponseDtoV1> userInfo = userServiceV1.searchUser(username, role, page, size);
        return ResponseEntity.ok(ApiResponseData.success(userInfo));
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<ApiResponseData<UserInfoResponseDtoV1>> updateUser(@PathVariable UUID userId,
                                                                             @Valid @RequestBody UserUpdateRequestDto requestDto,
                                                                             AuthenticatedUser authenticatedUser) {

        UserInfoResponseDtoV1 userInfo = userServiceV1.updateUser(userId, userMapper.toDto(requestDto));
        return ResponseEntity.ok(ApiResponseData.success(userInfo));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponseData<Void>> deleteUser(@PathVariable UUID userId,
                                                            AuthenticatedUser authenticatedUser) {

        userServiceV1.deleteUser(userId);
        return ResponseEntity.ok(ApiResponseData.success(null));
    }
}
