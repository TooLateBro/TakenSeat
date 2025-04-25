package com.taken_seat.auth_service.application.service.user.v2;

import com.taken_seat.auth_service.application.dto.user.v2.UserInfoResponseDtoV2;

import java.util.UUID;

public interface UserServiceV2 {

    UserInfoResponseDtoV2 getUser(UUID userId);
}