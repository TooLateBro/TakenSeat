package com.taken_seat.auth_service.application.dto.auth;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder(access = AccessLevel.PRIVATE)
public class AuthLoginDto {

    private String email;
    private String password;

    public static AuthLoginDto create(String email, String password) {
        return AuthLoginDto.builder()
                .email(email)
                .password(password)
                .build();
    }
}
