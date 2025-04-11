package com.taken_seat.booking_service.ticket.infrastructure.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taken_seat.booking_service.ticket.application.dto.request.TicketCreateRequest;
import com.taken_seat.booking_service.ticket.application.dto.response.TicketCreateResponse;
import com.taken_seat.booking_service.ticket.application.dto.response.TicketPageResponse;
import com.taken_seat.booking_service.ticket.application.dto.response.TicketReadResponse;
import com.taken_seat.booking_service.ticket.application.service.TicketService;
import com.taken_seat.booking_service.ticket.domain.Ticket;
import com.taken_seat.booking_service.ticket.domain.repository.TicketRepository;
import com.taken_seat.common_service.dto.AuthenticatedUser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

	private final TicketRepository ticketRepository;

	@Override
	public TicketCreateResponse createTicket(AuthenticatedUser authenticatedUser, TicketCreateRequest request) {

		// TODO: FeignClient 로 공연 정보, 좌석 정보 받아오기
		Ticket ticket = Ticket.builder()
			.userId(authenticatedUser.getUserId())
			.bookingId(request.getBookingId())
			.build();

		Ticket saved = ticketRepository.save(ticket);

		return TicketCreateResponse.toDto(saved);
	}

	@Override
	@Transactional(readOnly = true)
	public TicketReadResponse readTicket(AuthenticatedUser authenticatedUser, UUID id) {

		Ticket ticket = ticketRepository.findByIdAndUserIdAndDeletedAtIsNull(id, authenticatedUser.getUserId())
			.orElseThrow(() -> new RuntimeException("존재하지 않는 티켓입니다."));

		return TicketReadResponse.toDto(ticket);
	}

	@Override
	@Transactional(readOnly = true)
	public TicketPageResponse readTickets(AuthenticatedUser authenticatedUser, Pageable pageable) {

		Page<Ticket> page = ticketRepository.findAllByUserIdAndDeletedAtIsNull(pageable, authenticatedUser.getUserId());

		return TicketPageResponse.toDto(page);
	}
}