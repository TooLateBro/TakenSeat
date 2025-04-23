package com.taken_seat.auth_service.presentation.dto.auth;

import com.taken_seat.auth_service.application.dto.auth.AuthSignUpDto;
import com.taken_seat.auth_service.domain.vo.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AuthSignUpRequestDto(
        @NotBlank(message = "이름은 필수 입력 값입니다.")
        @Size(min = 4, max = 10, message = "이름은 최소 4자 이상, 10자 이하이어야 합니다.")
        @Pattern(regexp = "^[a-z0-9]+$", message = "이름은 알파벳 소문자(a~z)와 숫자(0~9)로만 구성되어야 합니다.")
        String username,

        @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
        @Size(min = 8, max = 15, message = "비밀번호는 최소 8자 이상, 15자 이하이어야 합니다.")
        @Pattern(
                regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+=-]).+$",
                message = "비밀번호는 알파벳 대소문자, 숫자, 특수문자를 모두 포함해야 합니다."
        )
        String password,

        @NotBlank(message = "이메일은 필수 입력 값입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        String email,

        @NotBlank(message = "전화번호는 필수 입력 값입니다.")
        @Pattern(
                regexp = "^01[016789]-\\d{3,4}-\\d{4}$",
                message = "전화번호는 형식에 맞게 입력해주세요. 예: 010-1234-5678"
        )
        String phone,

        Role role
) {
    public AuthSignUpDto toDto() {
        return AuthSignUpDto.create(username, email, phone, password, role);
    }
}