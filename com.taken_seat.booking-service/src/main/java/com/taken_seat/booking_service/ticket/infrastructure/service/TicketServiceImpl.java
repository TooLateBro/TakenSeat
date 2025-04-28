package com.taken_seat.booking_service.ticket.infrastructure.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taken_seat.booking_service.common.message.TicketRequestMessage;
import com.taken_seat.booking_service.common.service.RedisService;
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
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketServiceImpl implements TicketService {

	private final TicketRepository ticketRepository;
	private final TicketClientService ticketClientService;
	private final RedisService redisService;

	@Override
	@Transactional
	public void createTicket(TicketRequestMessage message) {

		log.info(
			"[Ticket] 티켓 생성 - 시도 | userId={}, bookingId={}",
			message.getUserId(),
			message.getBookingId()
		);

		TicketPerformanceClientResponse info = ticketClientService.getPerformanceInfo(
			message.getPerformanceId(),
			message.getPerformanceScheduleId(),
			message.getSeatId()
		);

		Ticket ticket = Ticket.builder()
			.userId(message.getUserId())
			.bookingId(message.getBookingId())
			.title(info.title())
			.name(info.name())
			.address(info.address())
			.startAt(info.startAt())
			.endAt(info.endAt())
			.seatRowNumber(info.rowNumber())
			.seatNumber(info.seatNumber())
			.seatType(info.seatType())
			.build();
		ticket.prePersist(message.getUserId());

		ticketRepository.save(ticket);

		log.info(
			"[Ticket] 티켓 생성 - 성공 | userId={}, bookingId={}",
			message.getUserId(),
			message.getBookingId()
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

	@Override
	@Transactional
	public void reissueTicket(TicketRequestMessage message) {

		log.info("[Ticket] 티켓 재발급 - 시도 | userId={}, bookingId={}", message.getUserId(), message.getBookingId());

		Optional<Ticket> optional = ticketRepository.findByBookingId(message.getBookingId());

		if (optional.isPresent()) {
			Ticket ticket = optional.get();
			ticket.delete(message.getUserId());
			redisService.evictCache("readTicket", ticket.getId().toString());
		}

		createTicket(message);
		redisService.evictAllCaches("readTickets", message.getBookingId().toString());

		log.info("[Ticket] 티켓 재발급 - 성공 | userId={}, bookingId={}", message.getUserId(), message.getBookingId());
	}
}