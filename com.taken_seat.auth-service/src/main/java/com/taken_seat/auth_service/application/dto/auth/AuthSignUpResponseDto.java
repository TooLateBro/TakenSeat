package com.taken_seat.auth_service.application.dto.auth;

import com.taken_seat.auth_service.domain.entity.user.User;
import com.taken_seat.auth_service.domain.vo.Role;
import lombok.*;

import java.util.UUID;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class AuthSignUpResponseDto {

    private UUID id;
    private String username;
    private String email;
    private String phone;
    private Role role;

    public static AuthSignUpResponseDto of(User user) {
        return AuthSignUpResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .build();
    }
}
