package com.taken_seat.auth_service.presentation.controller.auth;

import com.taken_seat.auth_service.application.dto.auth.AuthSignUpResponseDto;
import com.taken_seat.auth_service.application.service.auth.AuthService;
import com.taken_seat.auth_service.presentation.dto.auth.AuthSignUpRequestDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auths")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signUp")
    public ResponseEntity<AuthSignUpResponseDto> signUp(@Valid @RequestBody AuthSignUpRequestDto requestDto){
        AuthSignUpResponseDto userinfo = authService.signUp(requestDto.toDto());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userinfo);
    }
}
