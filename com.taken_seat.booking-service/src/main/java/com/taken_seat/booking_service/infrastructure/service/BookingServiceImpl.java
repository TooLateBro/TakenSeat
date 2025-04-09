package com.taken_seat.booking_service.infrastructure.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taken_seat.booking_service.application.BookingService;
import com.taken_seat.booking_service.application.RedissonService;
import com.taken_seat.booking_service.application.dto.request.BookingCreateRequest;
import com.taken_seat.booking_service.application.dto.response.BookingCreateResponse;
import com.taken_seat.booking_service.application.dto.response.BookingPageResponse;
import com.taken_seat.booking_service.application.dto.response.BookingReadResponse;
import com.taken_seat.booking_service.domain.Booking;
import com.taken_seat.booking_service.domain.repository.BookingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

	private final RedissonService redissonService;
	private final BookingRepository bookingRepository;

	@Override
	@Transactional
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

	@Override
	@Transactional(readOnly = true)
	public BookingReadResponse readBooking(UUID id) {

		// TODO: 사용자의 예매만 조회할 것
		Booking booking = bookingRepository.findById(id).orElseThrow(() -> new RuntimeException("존재하지 않는 예약입니다."));

		return BookingReadResponse.toDto(booking);
	}

	@Override
	@Transactional(readOnly = true)
	public BookingPageResponse readBookings(Pageable pageable, UUID userId) {

		// TODO: 사용자의 예매만 조회할 것
		Page<Booking> page = bookingRepository.findAllByUserId(pageable, userId);

		return BookingPageResponse.toDto(page);
	}

	@Override
	@Transactional
	public void updateBooking(UUID id) {
		Booking booking = bookingRepository.findById(id).orElseThrow(() -> new RuntimeException("없는 예약압니다."));

		if (booking.getCanceledAt() != null) {
			throw new RuntimeException("이미 취소된 예약입니다.");
		}

		booking.cancel();

		if (booking.getPaymentId() != null) {
			// TODO: 환불 요청 보내기
		}

		// TODO: 좌석 선점 해제 요청 보내기
	}
}