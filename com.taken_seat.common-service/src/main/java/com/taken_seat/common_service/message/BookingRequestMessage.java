package com.taken_seat.common_service.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor
public class BookingRequestMessage {
    private UUID userId;
    private UUID performanceId;
    private UUID performanceScheduleId;
}

