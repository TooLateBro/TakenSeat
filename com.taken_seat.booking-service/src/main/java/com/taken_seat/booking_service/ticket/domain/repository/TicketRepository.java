package com.taken_seat.booking_service.ticket.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taken_seat.booking_service.ticket.domain.Ticket;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, UUID> {

	Optional<Ticket> findByIdAndUserIdAndDeletedAtIsNull(UUID id, UUID userId);
}