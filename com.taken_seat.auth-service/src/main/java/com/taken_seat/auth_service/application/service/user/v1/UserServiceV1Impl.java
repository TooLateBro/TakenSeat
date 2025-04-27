package com.taken_seat.auth_service.application.service.user.v1;

import com.taken_seat.auth_service.application.dto.PageResponseDto;
import com.taken_seat.auth_service.application.dto.user.v1.UserInfoResponseDtoV1;
import com.taken_seat.auth_service.application.dto.user.UserUpdateDto;
import com.taken_seat.auth_service.application.dto.user.v1.UserMapper;
import com.taken_seat.auth_service.domain.entity.user.User;
import com.taken_seat.auth_service.domain.entity.user.UserCoupon;
import com.taken_seat.auth_service.domain.repository.user.UserQueryRepository;
import com.taken_seat.auth_service.domain.repository.user.UserRepository;
import com.taken_seat.auth_service.domain.repository.userCoupon.UserCouponRepository;
import com.taken_seat.common_service.exception.customException.AuthException;
import com.taken_seat.common_service.exception.customException.CouponException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UserServiceV1Impl implements UserServiceV1 {

    private final UserRepository userRepository;
    private final UserCouponRepository userCouponRepository;
    private final UserQueryRepository userQueryRepository;
    private final UserMapper userMapper;

    public UserServiceV1Impl(UserRepository userRepository, UserCouponRepository userCouponRepository,
                             UserQueryRepository userQueryRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userCouponRepository = userCouponRepository;
        this.userQueryRepository = userQueryRepository;
        this.userMapper = userMapper;
    }

    @Transactional(readOnly = true)
    @Override
    public UserInfoResponseDtoV1 getUser(UUID userId) {
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(()-> new AuthException(ResponseCode.USER_NOT_FOUND));

        return userMapper.userToUserInfoResponseDto(user);
    }

    @Transactional(readOnly = true)
    @Override
    @Cacheable(cacheNames = "searchUser", key = "#userId + '-' + #page+'-'+#size")
    public UserInfoResponseDtoV1 getUserDetails(UUID userId, int page, int size) {
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(()-> new AuthException(ResponseCode.USER_NOT_FOUND));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<UserCoupon> userCoupons = userCouponRepository.findCouponIdByUserIdAndIsActiveTrue(userId, pageable);

        return userMapper.userToUserInfoDetailsResponseDto(user, userCoupons);
    }

    @Transactional(readOnly = true)
    @Override
    @Cacheable(cacheNames = "searchUser", key = "#username + '-' + #role + '-' + #page + '-' + #size")
    public PageResponseDto<UserInfoResponseDtoV1> searchUser(String username, String role, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<User> userInfos = userQueryRepository.findAllByDeletedAtIsNull(username, role, pageable);

        if (userInfos.isEmpty()) {
            throw new AuthException(ResponseCode.USER_NOT_FOUND);
        }

        Page<UserInfoResponseDtoV1> userInfoPage = userInfos.map(user -> {
            List<UserCoupon> coupons = user.getUserCoupons();
            Page<UserCoupon> userCouponsPage = new PageImpl<>(coupons, pageable, coupons.size());
            return userMapper.userToUserInfoDetailsResponseDto(user, userCouponsPage);
        });

        return PageResponseDto.of(userInfoPage);
    }

    @Transactional
    @Override
    @CachePut(cacheNames = "userCache", key = "#result.userId")
    @CacheEvict(cacheNames = "searchUser", allEntries = true)
    public UserInfoResponseDtoV1 updateUser(UUID userId, UserUpdateDto dto) {
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(()-> new AuthException(ResponseCode.USER_NOT_FOUND));

        if(userRepository.findByEmail(String.valueOf(dto.email())).isPresent()){
            throw new AuthException(ResponseCode.USER_CONFLICT_EMAIL);
        }
        user.update(
                dto.username(),
                dto.email(),
                dto.phone(),
                dto.password(),
                dto.role(),
                userId
        );
        return userMapper.userToUserInfoResponseDto(user);
    }

    @Transactional
    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "userCache", allEntries = true),
            @CacheEvict(cacheNames = "searchUser", allEntries = true)
    })
    public void deleteUser(UUID userId) {
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(()-> new AuthException(ResponseCode.USER_NOT_FOUND));

        user.delete(userId);
    }

    @Transactional(readOnly = true)
    @Override
    public String getCoupon(UUID couponId) {
        UserCoupon userCoupon = userCouponRepository.findByCouponId(couponId)
                .orElseThrow(()-> new CouponException(ResponseCode.COUPON_QUANTITY_EXCEPTION));
        return "축하합니다!" + userCoupon + " 수령에 성공했습니다!";
    }
}
