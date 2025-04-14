package com.taken_seat.booking_service.booking.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.taken_seat.booking_service.booking.domain.Booking;

@Repository
public interface BookingRepository {

	Booking save(Booking booking);

	Optional<Booking> findById(UUID id);

	Optional<Booking> findByIdAndUserId(UUID id, UUID userId);

	Page<Booking> findAllByUserId(Pageable pageable, UUID userId);

	Optional<Booking> findByUserIdAndPerformanceId(UUID userId, UUID performanceId);
}