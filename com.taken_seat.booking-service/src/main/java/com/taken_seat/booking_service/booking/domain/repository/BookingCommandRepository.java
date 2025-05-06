package com.taken_seat.booking_service.booking.domain.repository;

import java.util.Optional;
import java.util.UUID;

import com.taken_seat.booking_service.booking.domain.BookingCommand;
import com.taken_seat.booking_service.booking.domain.BookingStatus;

public interface BookingCommandRepository {
	Optional<BookingCommand> findByIdAndUserId(UUID id, UUID userId);

	boolean isUniqueBooking(UUID userId, UUID performanceId, UUID performanceScheduleId, UUID scheduleSeatId);

	BookingCommand save(BookingCommand bookingCommand);

	Optional<BookingCommand> findById(UUID id);

	int countByUserIdAndPerformanceIdAndBookingStatus(UUID userId, UUID performanceId, BookingStatus bookingStatus);
}