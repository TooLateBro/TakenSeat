package com.taken_seat.auth_service.presentation.docs;

import com.taken_seat.auth_service.application.dto.auth.AuthLoginResponseDto;
import com.taken_seat.auth_service.application.dto.auth.AuthSignUpResponseDto;
import com.taken_seat.auth_service.presentation.dto.auth.AuthLoginRequestDto;
import com.taken_seat.auth_service.presentation.dto.auth.AuthSignUpRequestDto;
import com.taken_seat.common_service.dto.ApiResponseData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "사용자", description = "회원가입, 로그인을 담당하는 API 입니다.")
public interface AuthControllerDocs {

    @PostMapping("/api/v1/auths/signUp")
    @Operation(summary = "회원가입", description = "회원가입 API 입니다.")
    ResponseEntity<ApiResponseData<AuthSignUpResponseDto>> signUp(@Valid @RequestBody AuthSignUpRequestDto requestDto);

    @PostMapping("/api/v1/auths/login")
    @Operation(summary = "로그인", description = "로그인 API 입니다.")
    ResponseEntity<ApiResponseData<AuthLoginResponseDto>> login(@Valid @RequestBody AuthLoginRequestDto requestDto);
}
