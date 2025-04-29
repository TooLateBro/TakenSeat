package com.taken_seat.booking_service.booking.infrastructure.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.taken_seat.booking_service.booking.domain.BookingCommand;
import com.taken_seat.booking_service.booking.domain.repository.BookingRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class BookingRepositoryImpl implements BookingRepository {

	private final BookingJpaRepository bookingJpaRepository;

	@Override
	public BookingCommand save(BookingCommand bookingCommand) {
		return bookingJpaRepository.save(bookingCommand);
	}

	@Override
	public Optional<BookingCommand> findById(UUID id) {
		return bookingJpaRepository.findByIdAndDeletedAtIsNull(id);
	}

	@Override
	public Optional<BookingCommand> findByIdAndUserId(UUID id, UUID userId) {
		return bookingJpaRepository.findByIdAndUserIdAndDeletedAtIsNull(id, userId);
	}

	@Override
	public Page<BookingCommand> findAllByUserId(Pageable pageable, UUID userId) {
		return bookingJpaRepository.findAllByUserIdAndDeletedAtIsNull(pageable, userId);
	}

	@Override
	public Optional<BookingCommand> findByUserIdAndPerformanceId(UUID userId, UUID performanceId) {
		return bookingJpaRepository.findByUserIdAndPerformanceIdAndDeletedAtIsNull(userId, performanceId);
	}

	@Override
	public boolean isUniqueBooking(UUID userId, UUID performanceId, UUID performanceScheduleId, UUID seatId) {
		return bookingJpaRepository.existsByUserIdAndPerformanceIdAndPerformanceScheduleIdAndScheduleSeatIdAndDeletedAtIsNullAndCanceledAtIsNull(
			userId, performanceId, performanceScheduleId, seatId);
	}
}