package com.taken_seat.booking_service.ticket.infrastructure.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.taken_seat.common_service.dto.AuthenticatedUser;
import com.taken_seat.booking_service.ticket.application.dto.request.TicketCreateRequest;
import com.taken_seat.booking_service.ticket.application.service.TicketService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TicketConsumer {

	private final TicketService ticketService;

	@KafkaListener(topics = "ticket-topic", groupId = "ticket-group")
	public void createTicket(AuthenticatedUser authenticatedUser, TicketCreateRequest request) {
		ticketService.createTicket(authenticatedUser, request);
	}
}