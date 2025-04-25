package com.taken_seat.auth_service.application.dto.auth;

import com.taken_seat.common_service.aop.vo.Role;

public record AuthSignUpDto (
         String username,
         String email,
         String phone,
         String password,
         Role role
){
    public static AuthSignUpDto create(String username, String email, String phone, String password, Role role) {
        return new AuthSignUpDto(username, email, phone, password, role);
    }
}