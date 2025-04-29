package com.taken_seat.auth_service.application.dto.user.v1;

import com.taken_seat.common_service.aop.vo.Role;

public record UserUpdateDto(
        String username,
        String email,
        String phone,
        String password,
        Role role
){}
