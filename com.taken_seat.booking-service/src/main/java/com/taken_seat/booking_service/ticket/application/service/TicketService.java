package com.taken_seat.booking_service.ticket.application.service;

import com.taken_seat.booking_service.ticket.application.dto.request.TicketCreateRequest;
import com.taken_seat.booking_service.ticket.application.dto.response.TicketCreateResponse;
import com.taken_seat.common_service.dto.AuthenticatedUser;

public interface TicketService {
	TicketCreateResponse createTicket(AuthenticatedUser authenticatedUser, TicketCreateRequest request);

}