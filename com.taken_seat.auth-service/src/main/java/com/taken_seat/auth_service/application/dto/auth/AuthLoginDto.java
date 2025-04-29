package com.taken_seat.auth_service.application.dto.auth;

public record AuthLoginDto(
        String email,
        String password
){}
