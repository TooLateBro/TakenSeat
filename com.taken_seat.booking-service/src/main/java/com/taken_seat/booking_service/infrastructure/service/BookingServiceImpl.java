package com.taken_seat.booking_service.infrastructure.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.taken_seat.booking_service.application.BookingService;
import com.taken_seat.booking_service.application.RedissonService;
import com.taken_seat.booking_service.application.dto.request.BookingCreateRequest;
import com.taken_seat.booking_service.application.dto.response.BookingCreateResponse;
import com.taken_seat.booking_service.domain.Booking;
import com.taken_seat.booking_service.domain.repository.BookingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

	private final RedissonService redissonService;
	private final BookingRepository bookingRepository;

	@Override
	public BookingCreateResponse createBooking(BookingCreateRequest request) {

		UUID userId = UUID.randomUUID();

		Booking booking = Booking.builder()
			.performanceScheduleId(request.getPerformanceScheduleId())
			.seatId(request.getSeatId())
			.build();

		redissonService.tryHoldSeat(userId, request.getSeatId());

		Booking saved = bookingRepository.save(booking);

		return BookingCreateResponse.toDto(saved);
	}
}