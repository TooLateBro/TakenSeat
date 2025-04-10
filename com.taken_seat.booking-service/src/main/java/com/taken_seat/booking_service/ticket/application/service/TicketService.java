package com.taken_seat.booking_service.ticket.application.service;

import com.taken_seat.booking_service.common.CustomUser;
import com.taken_seat.booking_service.ticket.application.dto.request.TicketCreateRequest;
import com.taken_seat.booking_service.ticket.application.dto.response.TicketCreateResponse;

public interface TicketService {
	TicketCreateResponse createTicket(CustomUser customUser, TicketCreateRequest request);

}