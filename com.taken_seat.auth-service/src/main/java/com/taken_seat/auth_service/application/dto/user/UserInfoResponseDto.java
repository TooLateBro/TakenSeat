package com.taken_seat.auth_service.application.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.taken_seat.auth_service.application.dto.PageResponseDto;
import com.taken_seat.auth_service.domain.entity.user.User;
import com.taken_seat.auth_service.domain.entity.user.UserCoupon;
import com.taken_seat.auth_service.domain.vo.Role;
import lombok.*;
import org.springframework.data.domain.Page;

import java.util.UUID;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL) // null 필드 제외
public class UserInfoResponseDto {

    private UUID userId;
    private String username;
    private String email;
    private String phone;
    private Role role;
    private PageResponseDto<UUID> userCoupons;

    public static UserInfoResponseDto of(User user) {
        return UserInfoResponseDto.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .build();
    }

    public static UserInfoResponseDto listOf(User user, Page<UserCoupon> userCoupons) {
        PageResponseDto<UUID> couponsPage = PageResponseDto.of(userCoupons.map(UserCoupon::getCouponId));
        return UserInfoResponseDto.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .userCoupons(couponsPage)
                .build();
    }
}
