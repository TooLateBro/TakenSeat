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
public class PaymentAuthMessage {

    private UUID userId;
    private UUID couponId;
    private Integer count;
}
