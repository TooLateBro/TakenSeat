package com.taken_seat.booking_service.ticket.infrastructure.service;

import org.springframework.stereotype.Service;

import com.taken_seat.booking_service.common.CustomUser;
import com.taken_seat.booking_service.ticket.application.dto.request.TicketCreateRequest;
import com.taken_seat.booking_service.ticket.application.dto.response.TicketCreateResponse;
import com.taken_seat.booking_service.ticket.application.service.TicketService;
import com.taken_seat.booking_service.ticket.domain.Ticket;
import com.taken_seat.booking_service.ticket.domain.repository.TicketRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

	private final TicketRepository ticketRepository;

	@Override
	public TicketCreateResponse createTicket(CustomUser customUser, TicketCreateRequest request) {

		// TODO: FeignClient 로 공연 정보, 좌석 정보 받아오기
		Ticket ticket = Ticket.builder()
			.userId(customUser.getUserId())
			.bookingId(request.getBookingId())
			.build();

		Ticket saved = ticketRepository.save(ticket);

		return TicketCreateResponse.toDto(saved);
	}
}