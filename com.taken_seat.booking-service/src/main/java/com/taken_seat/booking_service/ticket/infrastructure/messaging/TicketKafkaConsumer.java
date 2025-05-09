package com.taken_seat.booking_service.ticket.infrastructure.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.taken_seat.booking_service.common.message.BookingQueryMessage;
import com.taken_seat.booking_service.ticket.application.service.TicketService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TicketKafkaConsumer {

	private final TicketService ticketService;

	@KafkaListener(topics = "${kafka.topic.ticket-request}", groupId = "${kafka.consumer.group-id.ticket-service}")
	public void createTicket(BookingQueryMessage message) {

		ticketService.createTicket(message);
	}
}