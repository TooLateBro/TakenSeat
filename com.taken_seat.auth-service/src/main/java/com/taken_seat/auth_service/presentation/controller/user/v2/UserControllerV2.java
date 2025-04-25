package com.taken_seat.auth_service.presentation.controller.user.v2;

import com.taken_seat.auth_service.application.dto.PageResponseDto;
import com.taken_seat.auth_service.application.dto.user.v2.UserInfoResponseDtoV2;
import com.taken_seat.auth_service.application.service.user.v2.UserServiceV2;
import com.taken_seat.common_service.aop.annotation.RoleCheck;
import com.taken_seat.common_service.dto.ApiResponseData;
import com.taken_seat.common_service.dto.AuthenticatedUser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v2/users")
public class UserControllerV2{

    private final UserServiceV2 userServiceV2;

    public UserControllerV2(UserServiceV2 userServiceV2) {
        this.userServiceV2 = userServiceV2;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponseData<UserInfoResponseDtoV2>> getUser(@PathVariable UUID userId) {

        UserInfoResponseDtoV2 userInfo = userServiceV2.getUser(userId);
        return ResponseEntity.ok(ApiResponseData.success(userInfo));
    }

    @GetMapping("/details/{userId}")
    public ResponseEntity<ApiResponseData<UserInfoResponseDtoV2>> getUserDetails(@PathVariable UUID userId,
                                                                                 @RequestParam(defaultValue = "0") int page,
                                                                                 @RequestParam(defaultValue = "10") int size) {

        UserInfoResponseDtoV2 userInfo = userServiceV2.getUserDetails(userId, page, size);
        return ResponseEntity.ok(ApiResponseData.success(userInfo));
    }

    @GetMapping("/search")
    @RoleCheck()
    public ResponseEntity<ApiResponseData<PageResponseDto<UserInfoResponseDtoV2>>> searchUser(@RequestParam(required = false) String username,
                                                                                              @RequestParam(required = false) String role,
                                                                                              @RequestParam(defaultValue = "0") int page,
                                                                                              @RequestParam(defaultValue = "10") int size,
                                                                                              AuthenticatedUser authenticatedUser) {

        PageResponseDto<UserInfoResponseDtoV2> userInfo = userServiceV2.searchUser(username, role, page, size);
        return ResponseEntity.ok(ApiResponseData.success(userInfo));
    }
}
