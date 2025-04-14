package com.taken_seat.booking_service.ticket.application.service;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.taken_seat.booking_service.common.message.TicketRequestMessage;
import com.taken_seat.booking_service.ticket.application.dto.request.TicketCreateRequest;
import com.taken_seat.booking_service.ticket.application.dto.response.TicketCreateResponse;
import com.taken_seat.booking_service.ticket.application.dto.response.TicketPageResponse;
import com.taken_seat.booking_service.ticket.application.dto.response.TicketReadResponse;
import com.taken_seat.common_service.dto.AuthenticatedUser;

public interface TicketService {
	TicketCreateResponse createTicket(AuthenticatedUser authenticatedUser, TicketCreateRequest request);

	TicketReadResponse readTicket(AuthenticatedUser authenticatedUser, UUID id);

	TicketPageResponse readTickets(AuthenticatedUser authenticatedUser, Pageable pageable);

	@Transactional
	void createTicket(TicketRequestMessage message);
}