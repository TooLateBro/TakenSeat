package com.taken_seat.booking_service.ticket.presentation;

import com.taken_seat.booking_service.common.message.TicketRequestMessage;

public interface TicketConsumer {
	void createTicket(TicketRequestMessage message);

	void reissueTicket(TicketRequestMessage message);
}