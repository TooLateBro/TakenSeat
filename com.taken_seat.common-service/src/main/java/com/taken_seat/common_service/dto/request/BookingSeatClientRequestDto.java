package com.taken_seat.common_service.dto.request;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingSeatClientRequestDto {

	private UUID performanceId;
	private UUID performanceScheduleId;
	private UUID seatId;
}
