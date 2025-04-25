package com.taken_seat.auth_service.application.service.user.v2;

import com.taken_seat.auth_service.application.dto.PageResponseDto;
import com.taken_seat.auth_service.application.dto.user.v2.UserInfoResponseDtoV2;

import java.util.UUID;

public interface UserServiceV2 {

    UserInfoResponseDtoV2 getUser(UUID userId);

    UserInfoResponseDtoV2 getUserDetails(UUID userId, int page, int size);

    PageResponseDto<UserInfoResponseDtoV2> searchUser(String username, String role, int page, int size);
}