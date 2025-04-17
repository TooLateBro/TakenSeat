package com.taken_seat.booking_service.booking.infrastructure.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.taken_seat.booking_service.booking.domain.Booking;
import com.taken_seat.booking_service.booking.domain.repository.BookingRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class BookingRepositoryImpl implements BookingRepository {

	private final BookingJpaRepository bookingJpaRepository;

	@Override
	public Booking save(Booking booking) {
		return bookingJpaRepository.save(booking);
	}

	@Override
	public Optional<Booking> findById(UUID id) {
		return bookingJpaRepository.findByIdAndDeletedAtIsNull(id);
	}

	@Override
	public Optional<Booking> findByIdAndUserId(UUID id, UUID userId) {
		return bookingJpaRepository.findByIdAndUserIdAndDeletedAtIsNull(id, userId);
	}

	@Override
	public Page<Booking> findAllByUserId(Pageable pageable, UUID userId) {
		return bookingJpaRepository.findAllByUserIdAndDeletedAtIsNull(pageable, userId);
	}

	@Override
	public Optional<Booking> findByUserIdAndPerformanceId(UUID userId, UUID performanceId) {
		return bookingJpaRepository.findByUserIdAndPerformanceIdAndDeletedAtIsNull(userId, performanceId);
	}

	@Override
	public boolean isUniqueBooking(UUID userId, UUID performanceId, UUID performanceScheduleId, UUID seatId) {
		return bookingJpaRepository.existsByUserIdAndPerformanceIdAndPerformanceScheduleIdAndSeatIdAndDeletedAtIsNull(
			userId, performanceId, performanceScheduleId, seatId);
	}
}