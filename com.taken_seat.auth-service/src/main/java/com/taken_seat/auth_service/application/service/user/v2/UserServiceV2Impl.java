package com.taken_seat.auth_service.application.service.user.v2;

import com.taken_seat.auth_service.application.dto.user.v2.UserMapper;
import com.taken_seat.auth_service.application.dto.user.v2.UserInfoResponseDtoV2;
import com.taken_seat.auth_service.domain.entity.user.User;
import com.taken_seat.auth_service.domain.repository.user.UserRepository;
import com.taken_seat.common_service.exception.customException.AuthException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserServiceV2Impl implements UserServiceV2 {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceV2Impl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Transactional(readOnly = true)
    @Override
    public UserInfoResponseDtoV2 getUser(UUID userId) {
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(()-> new AuthException(ResponseCode.USER_NOT_FOUND));

        UserInfoResponseDtoV2 responseDto = userMapper.userToUserInfoResponseDto(user);

        return responseDto;
    }
}
