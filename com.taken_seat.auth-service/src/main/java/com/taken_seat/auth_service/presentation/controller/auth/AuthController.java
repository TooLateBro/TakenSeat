package com.taken_seat.auth_service.presentation.controller.auth;

import com.taken_seat.auth_service.application.dto.auth.AuthLoginResponseDto;
import com.taken_seat.auth_service.application.dto.user.v1.UserInfoResponseDtoV1;
import com.taken_seat.auth_service.application.dto.user.v1.UserMapper;
import com.taken_seat.auth_service.application.service.auth.AuthService;
import com.taken_seat.auth_service.presentation.docs.AuthControllerDocs;
import com.taken_seat.auth_service.presentation.dto.auth.AuthLoginRequestDto;
import com.taken_seat.auth_service.presentation.dto.auth.AuthSignUpRequestDto;
import com.taken_seat.common_service.dto.ApiResponseData;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auths")
public class AuthController implements AuthControllerDocs {

    private final AuthService authService;
    private final UserMapper userMapper;

    public AuthController(AuthService authService, UserMapper userMapper) {
        this.authService = authService;
        this.userMapper = userMapper;
    }

    @PostMapping("/signUp")
    public ResponseEntity<ApiResponseData<UserInfoResponseDtoV1>> signUp(@Valid @RequestBody AuthSignUpRequestDto requestDto){
        UserInfoResponseDtoV1 userinfo = authService.signUp(userMapper.toDto(requestDto));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseData.success(userinfo));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponseData<AuthLoginResponseDto>> login(@Valid @RequestBody AuthLoginRequestDto requestDto){

        AuthLoginResponseDto userinfo = authService.login(userMapper.toDto(requestDto));

        return ResponseEntity.ok()
                .header("Authorization", userinfo.accessToken())
                .body(ApiResponseData.success(userinfo));
    }

    @GetMapping("/logout")
    public ResponseEntity<ApiResponseData<Void>> logout(@RequestHeader(value = "Authorization") String token) {
        authService.logout(token);
        return ResponseEntity.ok(ApiResponseData.success(null));
    }
}