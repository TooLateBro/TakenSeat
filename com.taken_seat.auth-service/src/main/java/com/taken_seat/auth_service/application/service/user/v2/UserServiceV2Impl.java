package com.taken_seat.auth_service.application.service.user.v2;

import com.taken_seat.auth_service.application.dto.PageResponseDto;
import com.taken_seat.auth_service.application.dto.user.v2.UserInfoResponseDtoV2;
import com.taken_seat.auth_service.application.dto.user.v2.UserMapper;
import com.taken_seat.auth_service.domain.entity.user.User;
import com.taken_seat.auth_service.domain.entity.user.UserCoupon;
import com.taken_seat.auth_service.domain.repository.user.UserQueryRepository;
import com.taken_seat.auth_service.domain.repository.user.UserRepository;
import com.taken_seat.auth_service.domain.repository.userCoupon.UserCouponRepository;
import com.taken_seat.common_service.exception.customException.AuthException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UserServiceV2Impl implements UserServiceV2 {

    private final UserRepository userRepository;
    private final UserCouponRepository userCouponRepository;
    private final UserQueryRepository userQueryRepository;
    private final UserMapper userMapper;

    public UserServiceV2Impl(UserRepository userRepository, UserCouponRepository userCouponRepository,
                             UserQueryRepository userQueryRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userCouponRepository = userCouponRepository;
        this.userQueryRepository = userQueryRepository;
        this.userMapper = userMapper;
    }

    @Transactional(readOnly = true)
    @Override
    public UserInfoResponseDtoV2 getUser(UUID userId) {
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(()-> new AuthException(ResponseCode.USER_NOT_FOUND));

        return userMapper.userToUserInfoResponseDto(user);
    }

    @Transactional(readOnly = true)
    @Override
    @Cacheable(cacheNames = "searchUser", key = "#userId + '-' + #page+'-'+#size")
    public UserInfoResponseDtoV2 getUserDetails(UUID userId, int page, int size) {
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(()-> new AuthException(ResponseCode.USER_NOT_FOUND));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<UserCoupon> userCoupons = userCouponRepository.findCouponIdByUserIdAndIsActiveTrue(userId, pageable);

        return userMapper.userToUserInfoDetailsResponseDto(user, userCoupons);
    }

    @Transactional(readOnly = true)
    @Override
    @Cacheable(cacheNames = "searchUser", key = "#username + '-' + #role + '-' + #page + '-' + #size")
    public PageResponseDto<UserInfoResponseDtoV2> searchUser(String username, String role, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<User> userInfos = userQueryRepository.findAllByDeletedAtIsNull(username, role, pageable);

        if (userInfos.isEmpty()) {
            throw new AuthException(ResponseCode.USER_NOT_FOUND);
        }

        Page<UserInfoResponseDtoV2> userInfoPage = userInfos.map(user -> {
            List<UserCoupon> coupons = user.getUserCoupons();
            Page<UserCoupon> userCouponsPage = new PageImpl<>(coupons, pageable, coupons.size());
            return userMapper.userToUserInfoDetailsResponseDto(user, userCouponsPage);
        });

        return PageResponseDto.of(userInfoPage);
    }
}
