package com.taken_seat.booking_service.ticket.infrastructure.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.taken_seat.booking_service.ticket.domain.Ticket;
import com.taken_seat.booking_service.ticket.domain.repository.TicketRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TicketRepositoryImpl implements TicketRepository {

	private final TicketJpaRepository ticketJpaRepository;

	@Override
	public Ticket save(Ticket ticket) {
		return ticketJpaRepository.save(ticket);
	}

	@Override
	public Optional<Ticket> findByIdAndUserId(UUID id, UUID userId) {
		return ticketJpaRepository.findByIdAndUserIdAndDeletedAtIsNull(id, userId);
	}

	@Override
	public Optional<Ticket> findByBookingId(UUID bookingId) {
		return ticketJpaRepository.findByBookingIdAndDeletedAtIsNull(bookingId);
	}

	@Override
	public Page<Ticket> findAllByUserId(Pageable pageable, UUID userId) {
		return ticketJpaRepository.findAllByUserIdAndDeletedAtIsNull(pageable, userId);
	}
}