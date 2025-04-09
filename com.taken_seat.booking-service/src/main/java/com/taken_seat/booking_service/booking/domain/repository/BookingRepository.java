package com.taken_seat.booking_service.booking.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taken_seat.booking_service.booking.domain.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {
	Optional<Booking> findByIdAndUserIdAndDeletedAtIsNull(UUID id, UUID userId);

	Page<Booking> findAllByUserIdAndDeletedAtIsNull(Pageable pageable, UUID userId);
}