package com.taken_seat.auth_service.application.dto.auth;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class AuthLoginResponseDto {

    private String accessToken;
    private String refreshToken;

    public static AuthLoginResponseDto of(String accessToken, String refreshToken) {
        return AuthLoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
