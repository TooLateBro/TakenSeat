package com.taken_seat.auth_service.presentation.dto.auth;

import com.taken_seat.auth_service.application.dto.auth.AuthLoginDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthLoginRequestDto {

    private String email;
    private String password;

    public AuthLoginDto toDto(){
        return AuthLoginDto.create(this.email, this.password);
    }
}
