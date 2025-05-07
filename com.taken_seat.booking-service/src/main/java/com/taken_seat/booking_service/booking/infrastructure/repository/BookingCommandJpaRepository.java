package com.taken_seat.booking_service.booking.infrastructure.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taken_seat.booking_service.booking.domain.BookingCommand;
import com.taken_seat.booking_service.booking.domain.BookingStatus;

public interface BookingCommandJpaRepository extends JpaRepository<BookingCommand, UUID> {
	boolean existsByUserIdAndPerformanceIdAndPerformanceScheduleIdAndScheduleSeatIdAndCanceledAtIsNullAndDeletedAtIsNull(
		UUID userId, UUID performanceID, UUID performanceScheduleId, UUID scheduleSeatId);

	Optional<BookingCommand> findByIdAndUserId(UUID id, UUID userId);

	Optional<BookingCommand> findByIdAndDeletedAtIsNull(UUID id);

	int countByUserIdAndPerformanceIdAndBookingStatus(UUID userId, UUID performanceId, BookingStatus bookingStatus);
}