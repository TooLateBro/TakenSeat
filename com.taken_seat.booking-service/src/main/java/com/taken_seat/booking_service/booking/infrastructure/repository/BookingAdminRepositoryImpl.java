package com.taken_seat.booking_service.booking.infrastructure.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.taken_seat.booking_service.booking.domain.Booking;
import com.taken_seat.booking_service.booking.domain.repository.BookingAdminRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class BookingAdminRepositoryImpl implements BookingAdminRepository {

	private final BookingAdminJpaRepository bookingAdminJpaRepository;

	@Override
	public Optional<Booking> findById(UUID id) {
		return bookingAdminJpaRepository.findById(id);
	}

	@Override
	public Page<Booking> findAll(Pageable pageable) {
		return bookingAdminJpaRepository.findAll(pageable);
	}
}