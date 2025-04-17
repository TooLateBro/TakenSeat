package com.taken_seat.booking_service.common.message;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor
public class TicketRequestMessage {
	private UUID userId;
	private UUID bookingId;
	private UUID performanceId;
	private UUID performanceScheduleId;
	private UUID seatId;
}