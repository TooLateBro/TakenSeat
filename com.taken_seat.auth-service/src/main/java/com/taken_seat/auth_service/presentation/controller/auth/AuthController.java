package com.taken_seat.auth_service.presentation.controller.auth;

import com.taken_seat.auth_service.application.dto.auth.AuthLoginResponseDto;
import com.taken_seat.auth_service.application.dto.auth.AuthSignUpResponseDto;
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

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signUp")
    public ResponseEntity<ApiResponseData<AuthSignUpResponseDto>> signUp(@Valid @RequestBody AuthSignUpRequestDto requestDto){
        AuthSignUpResponseDto userinfo = authService.signUp(requestDto.toDto());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseData.success(userinfo));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponseData<AuthLoginResponseDto>> login(@Valid @RequestBody AuthLoginRequestDto requestDto){

        AuthLoginResponseDto userinfo = authService.login(requestDto.toDto());

        return ResponseEntity.ok()
                .header("Authorization", userinfo.getAccessToken())
                .body(ApiResponseData.success(userinfo));
    }

    @GetMapping("/logout")
    public ResponseEntity<ApiResponseData<Void>> logout(@RequestHeader(value = "Authorization") String token) {
        authService.logout(token);
        return ResponseEntity.ok(ApiResponseData.success(null));
    }
}