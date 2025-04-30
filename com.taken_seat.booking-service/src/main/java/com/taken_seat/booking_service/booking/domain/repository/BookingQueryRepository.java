package com.taken_seat.booking_service.booking.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.taken_seat.booking_service.booking.domain.BookingQuery;

public interface BookingQueryRepository {
	BookingQuery save(BookingQuery bookingQuery);

	Optional<BookingQuery> findById(UUID bookingId);

	Optional<BookingQuery> findByIdAndUserId(UUID id, UUID userId);

	Page<BookingQuery> findAllByUserId(Pageable pageable, UUID userId);

	Page<BookingQuery> findAllByAdmin(Pageable pageable, UUID queryUserId);

	Optional<BookingQuery> findByUserIdAndPerformanceId(UUID userId, UUID performanceId);
}