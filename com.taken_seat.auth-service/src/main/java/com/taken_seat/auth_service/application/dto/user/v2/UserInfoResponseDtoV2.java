package com.taken_seat.auth_service.application.dto.user.v2;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.taken_seat.auth_service.application.dto.PageResponseDto;
import com.taken_seat.common_service.aop.vo.Role;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL) // null 필드 제외
public record UserInfoResponseDtoV2(
        UUID userId,
        String username,
        String email,
        String phone,
        Role role,
        Integer mileage,
        PageResponseDto<UUID> userCoupons
){}
