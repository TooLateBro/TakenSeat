package com.taken_seat.booking_service.booking.infrastructure.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.taken_seat.booking_service.booking.domain.BookingCommand;
import com.taken_seat.booking_service.booking.domain.repository.BookingCommandRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class BookingCommandRepositoryImpl implements BookingCommandRepository {

	private final BookingCommandJpaRepository bookingCommandJpaRepository;

	@Override
	public Optional<BookingCommand> findByIdAndUserId(UUID id, UUID userId) {
		return bookingCommandJpaRepository.findByIdAndUserId(id, userId);
	}

	@Override
	public boolean isUniqueBooking(UUID userId, UUID performanceId, UUID performanceScheduleId, UUID scheduleSeatId) {
		return bookingCommandJpaRepository.existsByUserIdAndPerformanceIdAndPerformanceScheduleIdAndScheduleSeatIdAndCanceledAtIsNullAndDeletedAtIsNull(
			userId, performanceId, performanceScheduleId, scheduleSeatId);
	}

	@Override
	public BookingCommand save(BookingCommand bookingCommand) {
		return bookingCommandJpaRepository.save(bookingCommand);
	}

	@Override
	public Optional<BookingCommand> findById(UUID id) {
		return bookingCommandJpaRepository.findByIdAndDeletedAtIsNull(id);
	}
}