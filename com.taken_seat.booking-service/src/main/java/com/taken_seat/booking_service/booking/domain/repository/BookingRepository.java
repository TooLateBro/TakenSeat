package com.taken_seat.booking_service.booking.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.taken_seat.booking_service.booking.domain.BookingCommand;

@Repository
public interface BookingRepository {

	BookingCommand save(BookingCommand bookingCommand);

	Optional<BookingCommand> findById(UUID id);

	Optional<BookingCommand> findByIdAndUserId(UUID id, UUID userId);

	Page<BookingCommand> findAllByUserId(Pageable pageable, UUID userId);

	Optional<BookingCommand> findByUserIdAndPerformanceId(UUID userId, UUID performanceId);

	boolean isUniqueBooking(UUID userId, UUID performanceId, UUID performanceScheduleId, UUID seatId);
}