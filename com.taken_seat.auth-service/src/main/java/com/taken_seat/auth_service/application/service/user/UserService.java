package com.taken_seat.auth_service.application.service.user;

import com.taken_seat.auth_service.application.dto.PageResponseDto;
import com.taken_seat.auth_service.application.dto.user.UserInfoResponseDto;
import com.taken_seat.auth_service.application.dto.user.UserUpdateDto;

import java.util.UUID;

public interface UserService {

    UserInfoResponseDto getUser(UUID userId);

    UserInfoResponseDto getUserDetails(UUID userId, int page, int size);

    PageResponseDto<UserInfoResponseDto> searchUser(String q, String role, int page, int size);

    UserInfoResponseDto updateUser(UUID userId, UserUpdateDto dto);

    void deleteUser(UUID userId);

    String getCoupon(UUID couponId);

}
