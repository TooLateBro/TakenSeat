package com.taken_seat.auth_service.application.dto.user.v1;

import com.taken_seat.auth_service.application.dto.PageResponseDto;
import com.taken_seat.common_service.aop.vo.Role;

import java.util.UUID;

public record UserDetailsResponseDtoV1(
        UUID id,
        String username,
        String email,
        String phone,
        Role role,
        Integer mileage,
        PageResponseDto<UUID> userCoupons
)
{}
