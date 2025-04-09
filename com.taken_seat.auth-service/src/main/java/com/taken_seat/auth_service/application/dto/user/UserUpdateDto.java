package com.taken_seat.auth_service.application.dto.user;

import com.taken_seat.auth_service.domain.vo.Role;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder(access = AccessLevel.PRIVATE)
public class UserUpdateDto {

    private String username;
    private String email;
    private String phone;
    private String password;
    private Role role;

    public static UserUpdateDto create(String username, String email, String phone, String password, Role role) {
        return UserUpdateDto.builder()
                .username(username)
                .email(email)
                .phone(phone)
                .password(password)
                .role(role)
                .build();
    }
}
