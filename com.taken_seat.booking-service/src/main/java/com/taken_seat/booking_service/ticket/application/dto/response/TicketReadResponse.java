package com.taken_seat.booking_service.ticket.application.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.taken_seat.booking_service.ticket.domain.Ticket;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TicketReadResponse {
	private UUID id;
	private UUID bookingId;
	private String title;
	private String name;
	private String address;
	private String rowNumber;
	private String seatNumber;
	private String seatType;
	private LocalDateTime startAt;
	private LocalDateTime endAt;

	public static TicketReadResponse toDto(Ticket ticket) {
		return TicketReadResponse.builder()
			.id(ticket.getId())
			.bookingId(ticket.getBookingId())
			.title(ticket.getTitle())
			.name(ticket.getName())
			.address(ticket.getAddress())
			.startAt(ticket.getStartAt())
			.endAt(ticket.getEndAt())
			.rowNumber(ticket.getRowNumber())
			.seatNumber(ticket.getSeatNumber())
			.seatType(ticket.getSeatType())
			.build();
	}
}