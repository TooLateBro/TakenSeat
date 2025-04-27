package com.taken_seat.common_service.dto.request;

import java.util.UUID;

public record BookingSeatClientRequestDto(
	UUID performanceId,
	UUID performanceScheduleId,
	UUID scheduleSeatId
) {
}
