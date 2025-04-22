package com.taken_seat.auth_service.unit;

import com.taken_seat.auth_service.application.dto.auth.AuthSignUpResponseDto;
import com.taken_seat.auth_service.application.service.auth.AuthServiceImpl;
import com.taken_seat.auth_service.domain.entity.user.User;
import com.taken_seat.auth_service.domain.repository.user.UserRepository;
import com.taken_seat.auth_service.domain.vo.Role;
import com.taken_seat.auth_service.presentation.dto.auth.AuthSignUpRequestDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    private Validator validator;

    @BeforeEach
    public void setUp() {
        // ValidatorFactory를 생성하여 Validator 객체 초기화
        // Jakarta Bean Validation을 사용하여 DTO의 유효성을 검사
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private User user;

    @Test
    @DisplayName("회원가입 성공 테스트")
    public void signUpSuccess() {
        String username = "testuser1";
        String password = "testPassword1!";
        String phone = "010-1111-1111";
        String email = "test@test.com";
        Role role = Role.ADMIN;

        AuthSignUpRequestDto requestDto = AuthSignUpRequestDto.builder()
                .username(username)
                .password(password)
                .phone(phone)
                .email(email)
                .role(role)
                .build();

        // DTO 객체의 유효성 검사 수행
        // @NotNull, @Email, @Pattern 등의 어노테이션에 따라 검사
        Set<ConstraintViolation<AuthSignUpRequestDto>> constraintViolations = validator.validate(requestDto);

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(bCryptPasswordEncoder.encode(password)).thenReturn("encodedPassword");

        AuthSignUpResponseDto result = authService.signUp(requestDto.toDto());

        assertNotNull(result);
        // 유효성 검사에서 위반 사항이 없는지 확인
        // constraintViolations가 비어 있어야 함
        assertTrue(constraintViolations.isEmpty());
        assertEquals(email, result.getEmail());
    }
    @Test
    @DisplayName("회원가입 실패 테스트")
    public void signUpFail() {
        String username = "test";
        String password = "test";
        String phone = "01011111111";
        String email = "testtestcom";
        Role role = Role.ADMIN;

        AuthSignUpRequestDto requestDto = AuthSignUpRequestDto.builder()
                .username(username)
                .password(password)
                .phone(phone)
                .email(email)
                .role(role)
                .build();

        Set<ConstraintViolation<AuthSignUpRequestDto>> constraintViolations = validator.validate(requestDto);

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(bCryptPasswordEncoder.encode(password)).thenReturn("encodedPassword");

        AuthSignUpResponseDto result = authService.signUp(requestDto.toDto());

        assertNotNull(result);
        assertFalse(constraintViolations.isEmpty());
        assertEquals(email, result.getEmail());
    }
}