package com.taken_seat.booking_service.ticket.application.messaging;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketProducer {

	private final KafkaTemplate<String, Object> kafkaTemplate;

	public void sendTicketComplete(String ticketId) {

	}
}