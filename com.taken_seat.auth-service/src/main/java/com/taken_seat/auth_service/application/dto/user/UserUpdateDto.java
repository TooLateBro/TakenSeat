package com.taken_seat.auth_service.application.dto.user;

import com.taken_seat.common_service.aop.vo.Role;

public record UserUpdateDto(
        String username,
        String email,
        String phone,
        String password,
        Role role
){

    public static UserUpdateDto create(String username, String email, String phone, String password, Role role) {
        return new UserUpdateDto(username, email, phone, password, role);
    }
}
