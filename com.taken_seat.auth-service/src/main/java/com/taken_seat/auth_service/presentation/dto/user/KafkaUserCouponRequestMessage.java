package com.taken_seat.auth_service.presentation.dto.user;

import com.taken_seat.auth_service.domain.entity.user.UserCoupon;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KafkaUserCouponRequestMessage {

    private UUID userId;
    private UserCoupon userCoupon;
}
