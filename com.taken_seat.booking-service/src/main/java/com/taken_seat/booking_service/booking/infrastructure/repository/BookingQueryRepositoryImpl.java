package com.taken_seat.booking_service.booking.infrastructure.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.taken_seat.booking_service.booking.domain.BookingQuery;
import com.taken_seat.booking_service.booking.domain.repository.BookingQueryRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class BookingQueryRepositoryImpl implements BookingQueryRepository {

	private final BookingQueryJpaRepository bookingQueryJpaRepository;

	@Override
	public BookingQuery save(BookingQuery bookingQuery) {
		return bookingQueryJpaRepository.save(bookingQuery);
	}

	@Override
	public Optional<BookingQuery> findById(UUID bookingId) {
		return bookingQueryJpaRepository.findByIdAndDeletedAtIsNull(bookingId);
	}

	@Override
	public Optional<BookingQuery> findByIdAndUserId(UUID id, UUID userId) {
		return bookingQueryJpaRepository.findByIdAndUserIdAndDeletedAtIsNull(id, userId);
	}

	@Override
	public Page<BookingQuery> findAllByUserId(Pageable pageable, UUID userId) {
		return bookingQueryJpaRepository.findAllByUserIdAndDeletedAtIsNull(pageable, userId);
	}

	@Override
	public Page<BookingQuery> findAllByAdmin(Pageable pageable, UUID queryUserId) {
		return bookingQueryJpaRepository.findAllByUserId(pageable, queryUserId);
	}
}