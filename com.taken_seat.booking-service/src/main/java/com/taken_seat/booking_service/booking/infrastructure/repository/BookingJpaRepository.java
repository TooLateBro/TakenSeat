package com.taken_seat.booking_service.booking.infrastructure.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.taken_seat.booking_service.booking.domain.Booking;

public interface BookingJpaRepository extends JpaRepository<Booking, UUID> {

	Optional<Booking> findByIdAndUserIdAndDeletedAtIsNull(UUID id, UUID userId);

	Page<Booking> findAllByUserIdAndDeletedAtIsNull(Pageable pageable, UUID userId);

	Optional<Booking> findByUserIdAndPerformanceIdAndDeletedAtIsNull(UUID userId, UUID performanceId);

	Optional<Booking> findByIdAndDeletedAtIsNull(UUID id);
}