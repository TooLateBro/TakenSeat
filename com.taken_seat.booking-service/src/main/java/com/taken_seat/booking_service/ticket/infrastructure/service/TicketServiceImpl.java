package com.taken_seat.booking_service.ticket.infrastructure.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taken_seat.booking_service.common.message.TicketRequestMessage;
import com.taken_seat.booking_service.ticket.application.dto.response.TicketPageResponse;
import com.taken_seat.booking_service.ticket.application.dto.response.TicketReadResponse;
import com.taken_seat.booking_service.ticket.application.service.TicketClientService;
import com.taken_seat.booking_service.ticket.application.service.TicketService;
import com.taken_seat.booking_service.ticket.domain.Ticket;
import com.taken_seat.booking_service.ticket.domain.repository.TicketRepository;
import com.taken_seat.common_service.dto.AuthenticatedUser;
import com.taken_seat.common_service.dto.response.TicketPerformanceClientResponse;
import com.taken_seat.common_service.exception.customException.TicketException;
import com.taken_seat.common_service.exception.enums.ResponseCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

	private final TicketRepository ticketRepository;
	private final TicketClientService ticketClientService;

	@Override
	@Transactional
	public void createTicket(TicketRequestMessage message) {

		TicketPerformanceClientResponse info = ticketClientService.getPerformanceInfo(
			message.getPerformanceId(),
			message.getPerformanceScheduleId(),
			message.getSeatId()
		);

		Ticket ticket = Ticket.builder()
			.userId(message.getUserId())
			.bookingId(message.getBookingId())
			.title(info.getTitle())
			.name(info.getName())
			.address(info.getAddress())
			.startAt(info.getStartAt())
			.endAt(info.getEndAt())
			.seatRowNumber(info.getSeatRowNumber())
			.seatNumber(info.getSeatNumber())
			.seatType(info.getSeatType())
			.build();
		ticket.prePersist(message.getUserId());

		ticketRepository.save(ticket);
	}

	@Override
	@Transactional(readOnly = true)
	public TicketReadResponse readTicket(AuthenticatedUser authenticatedUser, UUID id) {

		Ticket ticket = ticketRepository.findByIdAndUserId(id, authenticatedUser.getUserId())
			.orElseThrow(() -> new TicketException(ResponseCode.TICKET_NOT_FOUND_EXCEPTION));

		return TicketReadResponse.toDto(ticket);
	}

	@Override
	@Transactional(readOnly = true)
	public TicketPageResponse readTickets(AuthenticatedUser authenticatedUser, Pageable pageable) {

		Page<Ticket> page = ticketRepository.findAllByUserId(pageable, authenticatedUser.getUserId());

		return TicketPageResponse.toDto(page);
	}

	@Override
	@Transactional
	public void deleteTicket(AuthenticatedUser authenticatedUser, UUID id) {

		Ticket ticket = ticketRepository.findByIdAndUserId(id, authenticatedUser.getUserId())
			.orElseThrow(() -> new TicketException(ResponseCode.TICKET_NOT_FOUND_EXCEPTION));

		ticket.delete(authenticatedUser.getUserId());
	}

	@Override
	@Transactional
	public void reissueTicket(TicketRequestMessage message) {

		Optional<Ticket> optional = ticketRepository.findByBookingId(message.getBookingId());

		if (optional.isPresent()) {
			Ticket ticket = optional.get();
			ticket.delete(message.getUserId());
		}

		createTicket(message);
	}
}