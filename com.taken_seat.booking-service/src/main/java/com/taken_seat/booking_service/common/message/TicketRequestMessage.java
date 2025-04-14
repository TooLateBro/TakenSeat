package com.taken_seat.booking_service.common.message;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TicketRequestMessage {
	private UUID userId;
	private UUID bookingId;
}