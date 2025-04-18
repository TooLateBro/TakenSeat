package com.taken_seat.auth_service.application.service.user;

import com.taken_seat.auth_service.application.dto.PageResponseDto;
import com.taken_seat.auth_service.application.dto.user.UserInfoResponseDto;
import com.taken_seat.auth_service.application.dto.user.UserUpdateDto;
import com.taken_seat.auth_service.domain.entity.user.User;
import com.taken_seat.auth_service.domain.entity.user.UserCoupon;
import com.taken_seat.auth_service.domain.repository.user.UserQueryRepository;
import com.taken_seat.auth_service.domain.repository.user.UserRepository;
import com.taken_seat.auth_service.domain.repository.userCoupon.UserCouponRepository;
import com.taken_seat.common_service.exception.customException.AuthException;
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
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final UserCouponRepository userCouponRepository;
    private final UserQueryRepository userQueryRepository;

    public UserServiceImpl(UserRepository userRepository, UserCouponRepository userCouponRepository, UserQueryRepository userQueryRepository) {
        this.userRepository = userRepository;
        this.userCouponRepository = userCouponRepository;
        this.userQueryRepository = userQueryRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public UserInfoResponseDto getUser(UUID userId) {
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(()-> new AuthException(ResponseCode.USER_NOT_FOUND));

        return UserInfoResponseDto.of(user);
    }

    @Transactional(readOnly = true)
    @Override
    @Cacheable(cacheNames = "searchUser", key = "#userId + '-' + #page+'-'+#size")
    public UserInfoResponseDto getUserDetails(UUID userId, int page, int size) {
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(()-> new AuthException(ResponseCode.USER_NOT_FOUND));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<UserCoupon> userCoupons = userCouponRepository.findCouponIdByUserIdAndIsActiveTrue(user.getId(), pageable);

        return UserInfoResponseDto.listOf(user, userCoupons);
    }

    @Transactional(readOnly = true)
    @Override
    @Cacheable(cacheNames = "searchUser", key = "#q + '-' + #role + '-' + #page + '-' + #size")
    public PageResponseDto<UserInfoResponseDto> searchUser(String q, String role, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<User> userInfos = userQueryRepository.findAllByDeletedAtIsNull(q, role, pageable);

        Page<UserInfoResponseDto> userInfoPage = userInfos.map(user -> {
            List<UserCoupon> coupons = user.getUserCoupons();
            Page<UserCoupon> userCouponsPage = new PageImpl<>(coupons, pageable, coupons.size());
            return UserInfoResponseDto.listOf(user, userCouponsPage);
        });

        return PageResponseDto.of(userInfoPage);
    }

    @Transactional
    @Override
    @CachePut(cacheNames = "userCache", key = "#result.userId")
    @CacheEvict(cacheNames = "searchUser", allEntries = true)
    public UserInfoResponseDto updateUser(UUID userId, UserUpdateDto dto) {
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(()-> new AuthException(ResponseCode.USER_NOT_FOUND));

        if(userRepository.findByEmail(String.valueOf(dto.getEmail())).isPresent()){
            throw new AuthException(ResponseCode.USER_BAD_EMAIL);
        }
        user.update(
                dto.getUsername(),
                dto.getEmail(),
                dto.getPhone(),
                dto.getPassword(),
                dto.getRole(),
                userId
        );
        return UserInfoResponseDto.of(user);
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
                .orElseThrow(()-> new IllegalArgumentException("쿠폰이 소진되어 수령에 실패했습니다."));
        return "축하합니다!";
    }
}
