package com.taken_seat.auth_service.application.service.user;

import com.taken_seat.auth_service.application.dto.PageResponseDto;
import com.taken_seat.auth_service.application.dto.user.UserInfoResponseDto;
import com.taken_seat.auth_service.domain.entity.user.User;
import com.taken_seat.auth_service.domain.entity.user.UserCoupon;
import com.taken_seat.auth_service.domain.repository.user.UserQueryRepository;
import com.taken_seat.auth_service.domain.repository.user.UserRepository;
import com.taken_seat.auth_service.domain.repository.userCoupon.UserCouponRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final UserCouponRepository userCouponRepository;
    private final UserQueryRepository userQueryRepository;

    public UserService(UserRepository userRepository, UserCouponRepository userCouponRepository, UserQueryRepository userQueryRepository) {
        this.userRepository = userRepository;
        this.userCouponRepository = userCouponRepository;
        this.userQueryRepository = userQueryRepository;
    }

    @Transactional(readOnly = true)
    public UserInfoResponseDto getUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return UserInfoResponseDto.of(user);
    }

    @Transactional(readOnly = true)
    public UserInfoResponseDto getUserDetails(UUID userId, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<UserCoupon> userCoupons = userCouponRepository.findByUserIdAndIsActiveFalse(user.getId(), pageable);

        return UserInfoResponseDto.listOf(user, userCoupons);
    }

    @Transactional(readOnly = true)
    public PageResponseDto<UserInfoResponseDto> searchUser(String q, String role, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<User> userInfos = userQueryRepository.findAllByDeletedAtIsNull(q, role, pageable);

        Page<UserInfoResponseDto> userInfoPage = userInfos.map(user -> {
            List<UserCoupon> coupons = user.getUserCoupons();
            Page<UserCoupon> userCouponsPage = new PageImpl<>(coupons, PageRequest.of(page, size), coupons.size());
            return UserInfoResponseDto.listOf(user, userCouponsPage);
        });

        return PageResponseDto.of(userInfoPage);
    }
}
