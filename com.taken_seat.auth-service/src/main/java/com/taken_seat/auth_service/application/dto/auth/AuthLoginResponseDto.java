package com.taken_seat.auth_service.application.dto.auth;

public record AuthLoginResponseDto(
        String accessToken,
        String refreshToken
) {
    public static AuthLoginResponseDto of(String accessToken, String refreshToken) {
        return new AuthLoginResponseDto(accessToken, refreshToken);
    }
}
