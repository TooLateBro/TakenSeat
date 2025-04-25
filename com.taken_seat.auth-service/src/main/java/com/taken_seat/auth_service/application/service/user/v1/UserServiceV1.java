package com.taken_seat.auth_service.application.service.user.v1;

import com.taken_seat.auth_service.application.dto.PageResponseDto;
import com.taken_seat.auth_service.application.dto.user.v1.UserInfoResponseDtoV1;
import com.taken_seat.auth_service.application.dto.user.UserUpdateDto;

import java.util.UUID;

public interface UserServiceV1 {

    UserInfoResponseDtoV1 getUser(UUID userId);

    UserInfoResponseDtoV1 getUserDetails(UUID userId, int page, int size);

    PageResponseDto<UserInfoResponseDtoV1> searchUser(String q, String role, int page, int size);

    UserInfoResponseDtoV1 updateUser(UUID userId, UserUpdateDto dto);

    void deleteUser(UUID userId);

    String getCoupon(UUID couponId);

}
