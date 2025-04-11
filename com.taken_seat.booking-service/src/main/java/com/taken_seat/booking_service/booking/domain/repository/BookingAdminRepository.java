package com.taken_seat.booking_service.booking.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.taken_seat.booking_service.booking.domain.Booking;

public interface BookingAdminRepository {
	Optional<Booking> findById(UUID id);

	Page<Booking> findAll(Pageable pageable);
}