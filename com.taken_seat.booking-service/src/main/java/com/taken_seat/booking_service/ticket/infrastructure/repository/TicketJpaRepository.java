package com.taken_seat.booking_service.ticket.infrastructure.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.taken_seat.booking_service.ticket.domain.Ticket;

public interface TicketJpaRepository extends JpaRepository<Ticket, UUID> {
	Optional<Ticket> findByIdAndUserIdAndDeletedAtIsNull(UUID id, UUID userId);

	Optional<Ticket> findByBookingIdAndDeletedAtIsNull(UUID bookingId);

	Page<Ticket> findAllByUserIdAndDeletedAtIsNull(Pageable pageable, UUID userId);
}