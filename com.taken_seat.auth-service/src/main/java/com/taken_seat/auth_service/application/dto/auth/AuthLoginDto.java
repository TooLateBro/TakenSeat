package com.taken_seat.auth_service.application.dto.auth;

public record AuthLoginDto(
        String email,
        String password) {

    public static AuthLoginDto create(String email, String password) {
        return new AuthLoginDto(email, password);
    }
}
