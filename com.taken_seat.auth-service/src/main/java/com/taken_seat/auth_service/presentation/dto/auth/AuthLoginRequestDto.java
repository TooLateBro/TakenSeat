package com.taken_seat.auth_service.presentation.dto.auth;

import com.taken_seat.auth_service.application.dto.auth.AuthLoginDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthLoginRequestDto (
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        @Schema(example = "example@example.com")
        String email,

        @NotBlank(message = "비밀번호는 필수입니다.")
        @Schema(example = "Password1!")
        String password
) {
    public AuthLoginDto toDto(){
        return AuthLoginDto.create(this.email, this.password);
    }
}
