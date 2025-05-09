package com.taken_seat.booking_service.ticket.infrastructure.service;

import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taken_seat.booking_service.common.message.BookingQueryMessage;
import com.taken_seat.booking_service.common.service.RedisService;
import com.taken_seat.booking_service.ticket.application.dto.response.TicketPageResponse;
import com.taken_seat.booking_service.ticket.application.dto.response.TicketReadResponse;
import com.taken_seat.booking_service.ticket.application.service.TicketService;
import com.taken_seat.booking_service.ticket.domain.Ticket;
import com.taken_seat.booking_service.ticket.domain.repository.TicketRepository;
import com.taken_seat.common_service.dto.AuthenticatedUser;
import com.taken_seat.common_service.exception.customException.TicketException;
import com.taken_seat.common_service.exception.enums.ResponseCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketServiceImpl implements TicketService {

	private final TicketRepository ticketRepository;
	private final RedisService redisService;

	@Override
	@Transactional
	public void createTicket(BookingQueryMessage message) {

		log.info(
			"[Ticket] 티켓 생성 - 시도 | userId={}, bookingId={}",
			message.userId(),
			message.id()
		);

		if (ticketRepository.existsByBookingId(message.id())) {
			throw new TicketException(ResponseCode.TICKET_DUPLICATED_EXCEPTION);
		}

		Ticket ticket = Ticket.builder()
			.userId(message.userId())
			.bookingId(message.id())
			.title(message.title())
			.name(message.name())
			.address(message.address())
			.rowNumber(message.rowNumber())
			.seatNumber(message.seatNumber())
			.seatType(message.seatType())
			.startAt(message.startAt())
			.endAt(message.endAt())
			.build();
		ticket.prePersist(message.userId());

		ticketRepository.save(ticket);

		redisService.evictAllCaches("readTickets", message.id().toString());

		log.info(
			"[Ticket] 티켓 생성 - 성공 | userId={}, bookingId={}",
			message.userId(),
			message.id()
		);
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(value = "readTicket", key = "#id")
	public TicketReadResponse readTicket(AuthenticatedUser authenticatedUser, UUID id) {

		log.info("[Ticket] 티켓 조회 - 시도 | userId={}", authenticatedUser.getUserId());

		Ticket ticket = ticketRepository.findByIdAndUserId(id, authenticatedUser.getUserId())
			.orElseThrow(() -> new TicketException(ResponseCode.TICKET_NOT_FOUND_EXCEPTION));

		log.info("[Ticket] 티켓 조회 - 성공 | userId={}", authenticatedUser.getUserId());

		return TicketReadResponse.toDto(ticket);
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(value = "readTickets", key = "#bookingId + ':' + #authenticatedUser.userId + ':' + #pageable.pageNumber + ':' + #pageable.pageSize + ':' + #pageable.sort")
	public TicketPageResponse readTickets(AuthenticatedUser authenticatedUser, UUID bookingId, Pageable pageable) {

		log.info("[Ticket] 티켓 리스트 조회 - 시도 | userId={}, bookingId={}", authenticatedUser.getUserId(), bookingId);

		Page<Ticket> page = ticketRepository.findAllByUserIdAndBookingId(pageable, authenticatedUser.getUserId(),
			bookingId);

		log.info("[Ticket] 티켓 리스트 조회 - 성공 | userId={}, bookingId={}", authenticatedUser.getUserId(), bookingId);

		return TicketPageResponse.toDto(page);
	}

	@Override
	@Transactional
	@CacheEvict(value = "readTicket", key = "#id")
	public void deleteTicket(AuthenticatedUser authenticatedUser, UUID id) {

		log.info("[Ticket] 티켓 삭제 - 시도 | userId={}, ticketId={}", authenticatedUser.getUserId(), id);

		Ticket ticket = ticketRepository.findByIdAndUserId(id, authenticatedUser.getUserId())
			.orElseThrow(() -> new TicketException(ResponseCode.TICKET_NOT_FOUND_EXCEPTION));

		ticket.delete(authenticatedUser.getUserId());
		redisService.evictAllCaches("readTickets", ticket.getBookingId().toString());

		log.info("[Ticket] 티켓 삭제 - 성공 | userId={}, ticketId={}", authenticatedUser.getUserId(), id);
	}
}