package com.taken_seat.auth_service.presentation.controller.user.v2;

import com.taken_seat.auth_service.application.dto.user.v2.UserInfoResponseDtoV2;
import com.taken_seat.auth_service.application.service.user.v2.UserServiceV2;
import com.taken_seat.common_service.dto.ApiResponseData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
