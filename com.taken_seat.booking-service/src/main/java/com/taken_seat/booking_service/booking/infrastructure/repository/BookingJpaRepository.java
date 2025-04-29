package com.taken_seat.booking_service.booking.infrastructure.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.taken_seat.booking_service.booking.domain.BookingCommand;

public interface BookingJpaRepository extends JpaRepository<BookingCommand, UUID> {

	Optional<BookingCommand> findByIdAndUserIdAndDeletedAtIsNull(UUID id, UUID userId);

	Page<BookingCommand> findAllByUserIdAndDeletedAtIsNull(Pageable pageable, UUID userId);

	Optional<BookingCommand> findByUserIdAndPerformanceIdAndDeletedAtIsNull(UUID userId, UUID performanceId);

	Optional<BookingCommand> findByIdAndDeletedAtIsNull(UUID id);

	boolean existsByUserIdAndPerformanceIdAndPerformanceScheduleIdAndScheduleSeatIdAndDeletedAtIsNullAndCanceledAtIsNull(
		UUID userId,
		UUID performanceId, UUID performanceScheduleId, UUID seatId);
}