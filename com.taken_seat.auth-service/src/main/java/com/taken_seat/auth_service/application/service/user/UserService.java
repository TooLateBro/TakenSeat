package com.taken_seat.auth_service.application.service.user;

import com.taken_seat.auth_service.application.dto.PageResponseDto;
import com.taken_seat.auth_service.application.dto.user.UserInfoResponseDto;
import com.taken_seat.auth_service.application.dto.user.UserUpdateDto;
import com.taken_seat.auth_service.domain.entity.user.User;
import com.taken_seat.auth_service.domain.entity.user.UserCoupon;
import com.taken_seat.auth_service.domain.repository.user.UserQueryRepository;
import com.taken_seat.auth_service.domain.repository.user.UserRepository;
import com.taken_seat.auth_service.domain.repository.userCoupon.UserCouponRepository;
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
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(()-> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return UserInfoResponseDto.of(user);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "searchCache", key = "#userId + '-' + #page+'-'+#size")
    public UserInfoResponseDto getUserDetails(UUID userId, int page, int size) {
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(()-> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<UserCoupon> userCoupons = userCouponRepository.findByUserIdAndIsActiveFalse(user.getId(), pageable);

        return UserInfoResponseDto.listOf(user, userCoupons);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "searchCache", key = "#q + '-' + #role + '-' + #page + '-' + #size")
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

    @Transactional
    @CachePut(cacheNames = "userCache", key = "#result.userId")
    @CacheEvict(cacheNames = "searchCache", allEntries = true)
    public UserInfoResponseDto updateUser(UUID userId, UserUpdateDto dto) {
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(()-> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if(userRepository.findByEmail(String.valueOf(dto.getEmail())).isPresent()){
            throw new IllegalArgumentException("이미 사용 중인 이메일 입니다.");
        }
        user.update(
                dto.getUsername(),
                dto.getEmail(),
                dto.getPhone(),
                dto.getPassword(),
                dto.getRole()
        );

        return UserInfoResponseDto.of(user);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = "userCache", allEntries = true),
            @CacheEvict(cacheNames = "searchCache", allEntries = true)
    })
    public void deleteUser(UUID userId) {
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(()-> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        user.del(userId);
    }
}
