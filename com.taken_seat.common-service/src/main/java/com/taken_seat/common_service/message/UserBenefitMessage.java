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
public class UserBenefitMessage {

    private UUID paymentId;
    private UUID userId;
    private UUID couponId;
    private Integer count;
}
