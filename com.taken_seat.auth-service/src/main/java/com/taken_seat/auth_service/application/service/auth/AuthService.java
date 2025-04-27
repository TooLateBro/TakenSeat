package com.taken_seat.auth_service.application.service.auth;

import com.taken_seat.auth_service.application.dto.auth.AuthLoginDto;
import com.taken_seat.auth_service.application.dto.auth.AuthLoginResponseDto;
import com.taken_seat.auth_service.application.dto.auth.AuthSignUpDto;
import com.taken_seat.auth_service.application.dto.user.v1.UserInfoResponseDtoV1;

public interface AuthService {

    UserInfoResponseDtoV1 signUp(AuthSignUpDto dto);

    AuthLoginResponseDto login(AuthLoginDto dto);

    void logout(String token);
}
