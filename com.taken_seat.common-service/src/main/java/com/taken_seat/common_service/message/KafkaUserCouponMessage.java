package com.taken_seat.common_service.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KafkaUserCouponMessage {

    private UUID userId;
    private UUID couponId;

    public static KafkaUserCouponMessage of(UUID userId, UUID couponId) {
        return KafkaUserCouponMessage.builder()
                .userId(userId)
                .couponId(couponId)
                .build();
    }
}
