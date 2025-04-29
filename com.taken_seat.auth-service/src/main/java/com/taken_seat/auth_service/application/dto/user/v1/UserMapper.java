package com.taken_seat.auth_service.application.dto.user.v1;

import com.taken_seat.auth_service.application.dto.PageResponseDto;
import com.taken_seat.auth_service.application.dto.auth.AuthLoginDto;
import com.taken_seat.auth_service.application.dto.auth.AuthSignUpDto;
import com.taken_seat.auth_service.domain.entity.mileage.Mileage;
import com.taken_seat.auth_service.domain.entity.user.User;
import com.taken_seat.auth_service.domain.entity.user.UserCoupon;
import com.taken_seat.auth_service.presentation.dto.auth.AuthLoginRequestDto;
import com.taken_seat.auth_service.presentation.dto.auth.AuthSignUpRequestDto;
import com.taken_seat.auth_service.presentation.dto.user.UserUpdateRequestDto;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.Comparator;
import java.util.UUID;

@Mapper(componentModel = "spring") // Spring 컨테이너에서 빈으로 등록해줌
public interface UserMapper {

    UserInfoResponseDtoV1 userToUserInfoResponseDto(User user);

    AuthLoginDto toDto(AuthLoginRequestDto dto);

    AuthSignUpDto toDto(AuthSignUpRequestDto dto);

    UserUpdateDto toDto(UserUpdateRequestDto dto);

    // 수동 구현이 필요한 매핑은 default 메서드로 작성
    default UserDetailsResponseDtoV1 userToUserInfoDetailsResponseDto(User user, Page<UserCoupon> userCoupons) {
        if (user == null) {
            return null;
        }
        PageResponseDto<UUID> couponsPage = PageResponseDto.of(userCoupons.map(UserCoupon::getCouponId));

        Integer latestMileage = user.getMileages().stream()
                .max(Comparator.comparing(Mileage::getUpdatedAt))
                .map(Mileage::getMileage)
                .orElse(0);

        return new UserDetailsResponseDtoV1(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                latestMileage,
                couponsPage
        );
    }
}
