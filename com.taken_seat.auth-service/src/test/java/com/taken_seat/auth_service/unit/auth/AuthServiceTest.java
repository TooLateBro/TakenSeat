package com.taken_seat.auth_service.unit.auth;

import com.taken_seat.auth_service.application.dto.auth.AuthLoginDto;
import com.taken_seat.auth_service.application.dto.auth.AuthLoginResponseDto;
import com.taken_seat.auth_service.application.dto.auth.AuthSignUpDto;
import com.taken_seat.auth_service.application.dto.user.v1.UserDetailsResponseDtoV1;
import com.taken_seat.auth_service.application.dto.user.v1.UserInfoResponseDtoV1;
import com.taken_seat.auth_service.application.dto.user.v1.UserMapper;
import com.taken_seat.auth_service.application.service.auth.AuthServiceImpl;
import com.taken_seat.auth_service.domain.entity.user.User;
import com.taken_seat.auth_service.domain.repository.user.UserRepository;
import com.taken_seat.auth_service.infrastructure.util.JwtUtil;
import com.taken_seat.auth_service.presentation.dto.auth.AuthLoginRequestDto;
import com.taken_seat.auth_service.presentation.dto.auth.AuthSignUpRequestDto;
import com.taken_seat.common_service.aop.vo.Role;
import com.taken_seat.common_service.exception.customException.AuthException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import io.jsonwebtoken.Claims;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private Claims claims;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AuthServiceImpl authService;

    private Validator validator;

    private User user;

    @BeforeEach
    public void setUp() {
        // ValidatorFactory를 생성하여 Validator 객체 초기화
        // Jakarta Bean Validation을 사용하여 DTO의 유효성을 검사
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        user = User.create(
                "testuser1","test@test.com","010-1111-1111"
                ,"testPassword1!", Role.ADMIN
        );
    }

    @Test
    @DisplayName("회원가입 성공 테스트")
    public void signUpSuccess() {
        String username = "testuser1";
        String password = "testPassword1!";
        String phone = "010-1111-1111";
        String email = "test@test.com";
        Role role = Role.ADMIN;

        AuthSignUpRequestDto requestDto = new AuthSignUpRequestDto(username, password, email, phone, role);

        Set<ConstraintViolation<AuthSignUpRequestDto>> constraintViolations = validator.validate(requestDto);

        AuthSignUpDto authSignUpDto = new AuthSignUpDto(username, email, phone, password, role);
        when(userMapper.toDto(any(AuthSignUpRequestDto.class))).thenReturn(authSignUpDto);

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(bCryptPasswordEncoder.encode(password)).thenReturn("encodedPassword");

        UserInfoResponseDtoV1 mappedDto = new UserInfoResponseDtoV1(
                user.getId(), user.getUsername(), user.getEmail(), user.getPhone(), user.getRole());
        when(userMapper.userToUserInfoResponseDto(any(User.class))).thenReturn(mappedDto);

        UserInfoResponseDtoV1 result = authService.signUp(userMapper.toDto(requestDto));

        assertNotNull(result);
        assertTrue(constraintViolations.isEmpty());
        assertEquals(email, result.email());
    }

    @Test
    @DisplayName("회원가입 실패 테스트")
    public void signUpFail() {
        String username = "test";
        String password = "test";
        String phone = "01011111111";
        String email = "testtestcom";
        Role role = Role.ADMIN;

        AuthSignUpRequestDto requestDto = new AuthSignUpRequestDto(username, password, email, phone, role);

        Set<ConstraintViolation<AuthSignUpRequestDto>> constraintViolations = validator.validate(requestDto);
        AuthSignUpDto authSignUpDto = new AuthSignUpDto(username, email, phone, password, role);
        when(userMapper.toDto(any(AuthSignUpRequestDto.class))).thenReturn(authSignUpDto);

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(bCryptPasswordEncoder.encode(password)).thenReturn("encodedPassword");
        UserInfoResponseDtoV1 mappedDto = new UserInfoResponseDtoV1(
                user.getId(), username, email, phone, role);
        when(userMapper.userToUserInfoResponseDto(any(User.class))).thenReturn(mappedDto);
        UserInfoResponseDtoV1 result = authService.signUp(userMapper.toDto(requestDto));

        assertNotNull(result);
        assertFalse(constraintViolations.isEmpty());
        assertEquals(email, result.email());
    }

    @Test
    @DisplayName("로그인 성공 테스트")
    public void loginSuccess() {
        String email = "test@test.com";
        String password = "testPassword1!";

        AuthLoginRequestDto requestDto = new AuthLoginRequestDto(email, password);

        AuthLoginDto authLoginDto = new AuthLoginDto(email, password);
        when(userMapper.toDto(any(AuthLoginRequestDto.class))).thenReturn(authLoginDto);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(bCryptPasswordEncoder.matches(password, "testPassword1!")).thenReturn(true);
        when(jwtUtil.createToken(user)).thenReturn("access_token");
        when(jwtUtil.createRefreshToken(user.getId())).thenReturn("refresh_token");
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // 실제 서비스 메서드 실행
        AuthLoginResponseDto result = authService.login(userMapper.toDto(requestDto));

        // 결과 검증
        assertNotNull(result);
        assertEquals("access_token", result.accessToken());  // token 검증
        assertEquals("refresh_token", result.refreshToken());  // token 검증
    }

    @Test
    @DisplayName("로그인 실패 테스트 - 이메일 존재하지 않음")
    public void loginFail_userNotFound() {
        String email = "fail@fail.com";
        String password = "failPassword1!";

        AuthLoginRequestDto requestDto = new AuthLoginRequestDto(email, password);

        AuthLoginDto authLoginDto = new AuthLoginDto(email, password);
        when(userMapper.toDto(any(AuthLoginRequestDto.class))).thenReturn(authLoginDto);

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        AuthException exception = assertThrows(AuthException.class, () -> {
            authService.login(userMapper.toDto(requestDto));
        });

        assertEquals(ResponseCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("로그인 실패 테스트 - 비밀번호 불일치")
    public void loginFail_wrongPassword() {
        String email = "test@test.com";
        String password = "wrongPassword1!";

        AuthLoginRequestDto requestDto = new AuthLoginRequestDto(email, password);

        AuthLoginDto authLoginDto = new AuthLoginDto(email, password);
        when(userMapper.toDto(any(AuthLoginRequestDto.class))).thenReturn(authLoginDto);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(bCryptPasswordEncoder.matches(password, user.getPassword())).thenReturn(false);

        AuthException exception = assertThrows(AuthException.class, () -> {
            authService.login(userMapper.toDto(requestDto));
        });

        assertEquals(ResponseCode.USER_BAD_PASSWORD, exception.getErrorCode());
    }

    @Test
    @DisplayName("로그아웃 성공 테스트")
    public void logoutSuccess() {
        String email = "test@test.com";
        String token = "Bearer faketoken";
        String accessToken = "faketoken";

        when(jwtUtil.extractToken(token)).thenReturn(accessToken);
        when(jwtUtil.parseClaims(accessToken)).thenReturn(claims);
        when(claims.get("email", String.class)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        authService.logout(token);

        verify(redisTemplate).delete(email + " :: refresh_token");
    }

    @Test
    @DisplayName("로그아웃 실패 테스트")
    public void logoutFail() {
        String email = "fake@fake.com";
        String token = "Bearer faketoken";
        String accessToken = "faketoken";

        when(jwtUtil.extractToken(token)).thenReturn(accessToken);
        when(jwtUtil.parseClaims(accessToken)).thenReturn(claims);
        when(claims.get("email", String.class)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        AuthException exception = assertThrows(AuthException.class, () -> {
            authService.logout(token);
        });
        assertEquals(ResponseCode.USER_NOT_FOUND, exception.getErrorCode());
    }
}