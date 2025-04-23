package com.taken_seat.auth_service.application.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.taken_seat.auth_service.application.dto.PageResponseDto;
import com.taken_seat.auth_service.domain.entity.mileage.Mileage;
import com.taken_seat.auth_service.domain.entity.user.User;
import com.taken_seat.auth_service.domain.entity.user.UserCoupon;
import com.taken_seat.auth_service.domain.vo.Role;
import org.springframework.data.domain.Page;

import java.util.Comparator;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL) // null 필드 제외
public record UserInfoResponseDto (
        UUID userId,
        String username,
        String email,
        String phone,
        Role role,
        Integer mileage,
        PageResponseDto<UUID> userCoupons
){

    public static UserInfoResponseDto of(User user) {
        return new UserInfoResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                null,
                null
        );
    }

    public static UserInfoResponseDto detailsOf(User user, Page<UserCoupon> userCoupons) {
        PageResponseDto<UUID> couponsPage = PageResponseDto.of(userCoupons.map(UserCoupon::getCouponId));

        Integer latestMileage = user.getMileages().stream()
                .max(Comparator.comparing(Mileage::getUpdatedAt))
                .map(Mileage::getMileage)
                .orElse(0);

        return new UserInfoResponseDto(
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
