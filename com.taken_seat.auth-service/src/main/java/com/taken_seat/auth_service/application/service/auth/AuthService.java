package com.taken_seat.auth_service.application.service.auth;

import com.taken_seat.auth_service.application.dto.auth.AuthLoginDto;
import com.taken_seat.auth_service.application.dto.auth.AuthLoginResponseDto;
import com.taken_seat.auth_service.application.dto.auth.AuthSignUpDto;
import com.taken_seat.auth_service.application.dto.auth.AuthSignUpResponseDto;

public interface AuthService {

    AuthSignUpResponseDto signUp(AuthSignUpDto dto);

    AuthLoginResponseDto login(AuthLoginDto dto);
}
