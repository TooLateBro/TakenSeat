package com.taken_seat.common_service.message;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KafkaUserInfoMessage {

    @Schema(example = "uuid")
    private UUID userId;

    @Schema(example = "uuid")
    private UUID couponId;

    @Schema(hidden = true)
    private Integer discount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(hidden = true)
    private LocalDateTime expiredAt;

    @Schema(hidden = true)
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
