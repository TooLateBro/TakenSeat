package com.taken_seat.auth_service.application.dto.user;

import com.taken_seat.auth_service.domain.entity.user.User;
import com.taken_seat.auth_service.domain.entity.user.UserCoupon;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KafkaUserInfoMessage {

    private UUID userId;
    private List<UserCoupon> couponId;

    public static KafkaUserInfoMessage of(User user) {
        return KafkaUserInfoMessage.builder()
                .userId(user.getId())
                .couponId(user.getUserCoupons())
                .build();
    }
}
