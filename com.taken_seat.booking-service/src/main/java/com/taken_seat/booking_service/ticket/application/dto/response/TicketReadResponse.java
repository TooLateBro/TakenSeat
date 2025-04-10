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
	private UUID performanceScheduleId;
	private String name;
	private String address;
	private LocalDateTime startAt;
	private LocalDateTime endAt;
	private UUID seatId;
	private String seatRowNumber;
	private String seatNumber;
	private String seatType;

	public static TicketReadResponse toDto(Ticket ticket) {
		return TicketReadResponse.builder()
			.id(ticket.getId())
			.bookingId(ticket.getBookingId())
			.performanceScheduleId(ticket.getPerformanceScheduleId())
			.name(ticket.getName())
			.address(ticket.getAddress())
			.startAt(ticket.getStartAt())
			.endAt(ticket.getEndAt())
			.seatId(ticket.getSeatId())
			.seatRowNumber(ticket.getSeatRowNumber())
			.seatNumber(ticket.getSeatNumber())
			.seatType(ticket.getSeatType())
			.build();
	}
}