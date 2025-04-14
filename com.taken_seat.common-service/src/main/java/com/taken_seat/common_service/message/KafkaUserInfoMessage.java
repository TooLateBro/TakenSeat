package com.taken_seat.common_service.message;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KafkaUserInfoMessage {

    private UUID userId;
    private UUID couponId;
    private Integer discount;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime expiredAt;
    private Status status = Status.PENDING;

    public enum Status {
        PENDING, SUCCEEDED, FAILED
    }

    public void success(UUID userId, UUID couponId, Integer discount, LocalDateTime expiredAt, Status status) {
        this.userId = userId;
        this.couponId = couponId;
        this.discount = discount;
        this.expiredAt = expiredAt;
        this.status = status;
    }
    public void failed(UUID userId, UUID couponId, Status status) {
        this.userId = userId;
        this.couponId = couponId;
        this.status = status;
    }
}
