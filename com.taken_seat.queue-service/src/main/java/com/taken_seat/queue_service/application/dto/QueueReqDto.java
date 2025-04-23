package com.taken_seat.queue_service.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class QueueReqDto {
    @NotNull(message = "공연 ID는 필수값입니다.")
    private UUID performanceId;

    @NotNull(message = "공연 회차 ID는 필수값입니다.")
    private UUID performanceScheduleId;
}
