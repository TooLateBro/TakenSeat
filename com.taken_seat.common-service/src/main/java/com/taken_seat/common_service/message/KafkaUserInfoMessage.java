package com.taken_seat.common_service.message;

import lombok.*;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KafkaUserInfoMessage {

    private UUID userId;
    private UUID couponId;

    @Setter
    private Status status = Status.PENDING;

    public enum Status {
        PENDING, SUCCEEDED, FAILED
    }
}
