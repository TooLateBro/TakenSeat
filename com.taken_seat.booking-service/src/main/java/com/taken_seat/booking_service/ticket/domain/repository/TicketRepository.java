package com.taken_seat.booking_service.ticket.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.taken_seat.booking_service.ticket.domain.Ticket;

@Repository
public interface TicketRepository {
	Ticket save(Ticket ticket);

	Optional<Ticket> findByIdAndUserId(UUID id, UUID userId);

	Optional<Ticket> findByBookingId(UUID bookingId);

	Page<Ticket> findAllByUserId(Pageable pageable, UUID userId);
}