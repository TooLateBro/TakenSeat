package com.taken_seat.booking_service.ticket.infrastructure.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.taken_seat.booking_service.common.message.TicketRequestMessage;
import com.taken_seat.booking_service.ticket.application.service.TicketService;
import com.taken_seat.booking_service.ticket.presentation.TicketConsumer;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TicketKafkaConsumer implements TicketConsumer {

	private final TicketService ticketService;

	@Override
	@KafkaListener(topics = "ticket.request", groupId = "ticket-service")
	public void createTicket(TicketRequestMessage message) {

		ticketService.createTicket(message);
	}
}