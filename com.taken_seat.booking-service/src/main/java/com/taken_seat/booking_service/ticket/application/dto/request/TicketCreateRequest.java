package com.taken_seat.booking_service.ticket.application.dto.request;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TicketCreateRequest {
	private UUID bookingId;
	private UUID performanceScheduleId;
	private UUID seatId;
}