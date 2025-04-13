package com.taken_seat.common_service.dto.request;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BookingSeatClientRequestDto {

	private UUID performanceId;
	private UUID performanceScheduleId;
	private UUID seatId;
}
