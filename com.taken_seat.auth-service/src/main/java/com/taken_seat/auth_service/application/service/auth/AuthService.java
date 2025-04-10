package com.taken_seat.auth_service.application.service.auth;

import com.taken_seat.auth_service.application.dto.auth.AuthLoginDto;
import com.taken_seat.auth_service.application.dto.auth.AuthLoginResponseDto;
import com.taken_seat.auth_service.application.dto.auth.AuthSignUpResponseDto;
import com.taken_seat.auth_service.application.dto.auth.AuthSignUpDto;
import com.taken_seat.auth_service.domain.entity.user.User;
import com.taken_seat.auth_service.domain.repository.user.UserRepository;
import com.taken_seat.auth_service.infrastructure.util.JwtUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtil jwtUtil;
    private final StringRedisTemplate stringRedisTemplate;

    public AuthService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, JwtUtil jwtUtil, StringRedisTemplate stringRedisTemplate) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtUtil = jwtUtil;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Transactional
    public AuthSignUpResponseDto signUp(AuthSignUpDto dto) {
        if(userRepository.findByEmail(dto.getEmail()).isPresent()){
            throw new IllegalArgumentException("이미 사용 중인 이메일 입니다.");
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
    public AuthLoginResponseDto login(AuthLoginDto dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일이 존재하지 않습니다."));

        if (!bCryptPasswordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtUtil.createToken(user);
        String refreshToken = jwtUtil.createRefreshToken(user.getId());

        stringRedisTemplate.opsForValue().set(user.getEmail()+" : refresh_token", refreshToken, 10, TimeUnit.SECONDS);

        return AuthLoginResponseDto.of(accessToken, refreshToken);
    }
}
