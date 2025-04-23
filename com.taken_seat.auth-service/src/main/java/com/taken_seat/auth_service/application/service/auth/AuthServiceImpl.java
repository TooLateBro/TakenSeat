package com.taken_seat.auth_service.application.service.auth;

import com.taken_seat.auth_service.application.dto.auth.AuthLoginDto;
import com.taken_seat.auth_service.application.dto.auth.AuthLoginResponseDto;
import com.taken_seat.auth_service.application.dto.auth.AuthSignUpDto;
import com.taken_seat.auth_service.application.dto.auth.AuthSignUpResponseDto;
import com.taken_seat.auth_service.domain.entity.user.User;
import com.taken_seat.auth_service.domain.repository.user.UserRepository;
import com.taken_seat.auth_service.infrastructure.util.JwtUtil;
import com.taken_seat.common_service.exception.customException.AuthException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import io.jsonwebtoken.Claims;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
public class AuthServiceImpl implements AuthService{

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;

    public AuthServiceImpl(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, JwtUtil jwtUtil, RedisTemplate<String, String> redisTemplate) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate;
    }

    @Transactional
    @Override
    public AuthSignUpResponseDto signUp(AuthSignUpDto dto) {
        if(userRepository.findByEmail(dto.email()).isPresent()){
            throw new AuthException(ResponseCode.USER_BAD_EMAIL);
        }
        User user = User.create(
                dto.username(), dto.email(),
                dto.phone(), bCryptPasswordEncoder.encode(dto.password()),
                dto.role()
        );
        userRepository.save(user);

        return AuthSignUpResponseDto.of(user);
    }

    @Transactional(readOnly = true)
    @Override
    public AuthLoginResponseDto login(AuthLoginDto dto) {
        User user = userRepository.findByEmail(dto.email())
                .orElseThrow(() -> new AuthException(ResponseCode.USER_NOT_FOUND));

        if (!bCryptPasswordEncoder.matches(dto.password(), user.getPassword())) {
            throw new AuthException(ResponseCode.USER_BAD_PASSWORD);
        }

        String accessToken = jwtUtil.createToken(user);
        String refreshToken = jwtUtil.createRefreshToken(user.getId());

        redisTemplate.opsForValue().set(user.getEmail()+" :: refresh_token", refreshToken, 1, TimeUnit.HOURS);

        return AuthLoginResponseDto.of(accessToken, refreshToken);
    }

    @Override
    public void logout(String token) {
        String accessToken = jwtUtil.extractToken(token);
        Claims claims = jwtUtil.parseClaims(accessToken);
        String email = claims.get("email", String.class);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException(ResponseCode.USER_NOT_FOUND));

        redisTemplate.delete(user.getEmail()+" :: refresh_token"); // 기존 refreshToken 삭제
    }
}

