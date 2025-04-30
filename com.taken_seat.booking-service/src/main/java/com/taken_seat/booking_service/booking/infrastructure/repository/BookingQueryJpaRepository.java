package com.taken_seat.booking_service.booking.infrastructure.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.taken_seat.booking_service.booking.domain.BookingQuery;

public interface BookingQueryJpaRepository extends JpaRepository<BookingQuery, UUID> {
	Optional<BookingQuery> findByIdAndDeletedAtIsNull(UUID id);

	Optional<BookingQuery> findByIdAndUserIdAndDeletedAtIsNull(UUID id, UUID userId);

	Page<BookingQuery> findAllByUserIdAndDeletedAtIsNull(Pageable pageable, UUID userId);

	Page<BookingQuery> findAllByUserId(Pageable pageable, UUID userId);

	Optional<BookingQuery> findByUserIdAndPerformanceIdAndDeletedAtIsNull(UUID userId, UUID performanceId);
}