package com.taken_seat.booking_service.ticket.application.dto.response;

import java.util.UUID;

import com.taken_seat.booking_service.ticket.domain.Ticket;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TicketCreateResponse {
	private UUID id;

	public static TicketCreateResponse toDto(Ticket ticket) {
		return TicketCreateResponse.builder()
			.id(ticket.getId())
			.build();
	}
}