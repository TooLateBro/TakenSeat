package com.taken_seat.auth_service.application.dto.auth;

import com.taken_seat.auth_service.domain.entity.user.User;
import com.taken_seat.auth_service.domain.vo.Role;

import java.util.UUID;

public record AuthSignUpResponseDto (
        UUID id,
        String username,
        String email,
        String phone,
        Role role
){

    public static AuthSignUpResponseDto of(User user) {
        return new AuthSignUpResponseDto(
                user.getId(), user.getUsername(), user.getEmail(), user.getPhone(), user.getRole()
        );
    }
}
