package com.taken_seat.auth_service.application.service.user;

import com.taken_seat.auth_service.application.dto.user.UserInfoResponseDto;
import com.taken_seat.auth_service.domain.entity.user.User;
import com.taken_seat.auth_service.domain.entity.user.UserCoupon;
import com.taken_seat.auth_service.domain.repository.user.UserRepository;
import com.taken_seat.auth_service.domain.repository.userCoupon.UserCouponRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final UserCouponRepository userCouponRepository;
    
    public UserService(UserRepository userRepository, UserCouponRepository userCouponRepository) {
        this.userRepository = userRepository;
        this.userCouponRepository = userCouponRepository;
    }

    public UserInfoResponseDto getUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return UserInfoResponseDto.of(user);
    }

    public UserInfoResponseDto getUserDetails(UUID userId, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<UserCoupon> userCoupons = userCouponRepository.findByUserIdAndIsActiveFalse(user.getId(), pageable);

        return UserInfoResponseDto.listOf(user, userCoupons);
    }

}
