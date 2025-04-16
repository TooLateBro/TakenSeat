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
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
class AuthServiceImpl implements AuthService{

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtil jwtUtil;
    private final StringRedisTemplate stringRedisTemplate;

    public AuthServiceImpl(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, JwtUtil jwtUtil, StringRedisTemplate stringRedisTemplate) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtUtil = jwtUtil;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Transactional
    @Override
    public AuthSignUpResponseDto signUp(AuthSignUpDto dto) {
        if(userRepository.findByEmail(dto.getEmail()).isPresent()){
            throw new AuthException(ResponseCode.USER_BAD_EMAIL);
        }
        User user = User.create(
                dto.getUsername(), dto.getEmail(),
                dto.getPhone(), bCryptPasswordEncoder.encode(dto.getPassword()),
                dto.getRole()
        );
        userRepository.save(user);

        return AuthSignUpResponseDto.of(user);
    }

    @Transactional(readOnly = true)
    @Override
    public AuthLoginResponseDto login(AuthLoginDto dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new AuthException(ResponseCode.USER_NOT_FOUND));

        if (!bCryptPasswordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new AuthException(ResponseCode.USER_BAD_PASSWORD);
        }

        String accessToken = jwtUtil.createToken(user);
        String refreshToken = jwtUtil.createRefreshToken(user.getId());

        stringRedisTemplate.opsForValue().set(user.getEmail()+" : refresh_token", refreshToken, 10, TimeUnit.SECONDS);

        return AuthLoginResponseDto.of(accessToken, refreshToken);
    }
}

