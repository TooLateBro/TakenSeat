package com.taken_seat.booking_service.ticket.application.service;

import java.util.UUID;

import org.springframework.data.domain.Pageable;

import com.taken_seat.booking_service.common.message.BookingQueryMessage;
import com.taken_seat.booking_service.ticket.application.dto.response.TicketPageResponse;
import com.taken_seat.booking_service.ticket.application.dto.response.TicketReadResponse;
import com.taken_seat.common_service.dto.AuthenticatedUser;

public interface TicketService {
	TicketReadResponse readTicket(AuthenticatedUser authenticatedUser, UUID id);

	TicketPageResponse readTickets(AuthenticatedUser authenticatedUser, UUID bookingId, Pageable pageable);

	void createTicket(BookingQueryMessage message);

	void deleteTicket(AuthenticatedUser authenticatedUser, UUID id);
}