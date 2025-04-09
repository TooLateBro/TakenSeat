package com.taken_seat.booking_service.booking.infrastructure.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taken_seat.booking_service.booking.application.BookingService;
import com.taken_seat.booking_service.booking.application.RedissonService;
import com.taken_seat.booking_service.booking.application.dto.request.BookingCreateRequest;
import com.taken_seat.booking_service.booking.application.dto.response.AdminBookingPageResponse;
import com.taken_seat.booking_service.booking.application.dto.response.AdminBookingReadResponse;
import com.taken_seat.booking_service.booking.application.dto.response.BookingCreateResponse;
import com.taken_seat.booking_service.booking.application.dto.response.BookingPageResponse;
import com.taken_seat.booking_service.booking.application.dto.response.BookingReadResponse;
import com.taken_seat.booking_service.booking.domain.Booking;
import com.taken_seat.booking_service.booking.domain.repository.BookingRepository;
import com.taken_seat.booking_service.common.CustomUser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

	private final RedissonService redissonService;
	private final BookingRepository bookingRepository;

	@Override
	@Transactional
	public BookingCreateResponse createBooking(CustomUser customUser, BookingCreateRequest request) {

		Booking booking = Booking.builder()
			.userId(customUser.getUserId())
			.performanceScheduleId(request.getPerformanceScheduleId())
			.seatId(request.getSeatId())
			.build();

		redissonService.tryHoldSeat(customUser.getUserId(), request.getSeatId());

		Booking saved = bookingRepository.save(booking);

		return BookingCreateResponse.toDto(saved);
	}

	@Override
	@Transactional(readOnly = true)
	public BookingReadResponse readBooking(CustomUser customUser, UUID id) {

		Booking booking = bookingRepository.findByIdAndUserId(id, customUser.getUserId())
			.orElseThrow(() -> new RuntimeException("존재하지 않는 예약입니다."));

		return BookingReadResponse.toDto(booking);
	}

	@Override
	@Transactional(readOnly = true)
	public BookingPageResponse readBookings(CustomUser customUser, Pageable pageable) {

		Page<Booking> page = bookingRepository.findAllByUserId(pageable, customUser.getUserId());

		return BookingPageResponse.toDto(page);
	}

	@Override
	@Transactional
	public void updateBooking(CustomUser customUser, UUID id) {

		Booking booking = bookingRepository.findByIdAndUserId(id, customUser.getUserId())
			.orElseThrow(() -> new RuntimeException("존재하지 않는 예약입니다."));

		if (booking.getCanceledAt() != null) {
			throw new RuntimeException("이미 취소된 예약입니다.");
		}

		booking.cancel();

		if (booking.getPaymentId() != null) {
			// TODO: 환불 요청 보내기
		}

		// TODO: 좌석 선점 해제 요청 보내기
	}

	@Override
	@Transactional
	public void deleteBooking(CustomUser customUser, UUID id) {

		Booking booking = bookingRepository.findByIdAndUserId(id, customUser.getUserId())
			.orElseThrow(() -> new RuntimeException("존재하지 않는 예약입니다."));

		if (booking.getCanceledAt() == null) {
			throw new RuntimeException("예약 취소 후 삭제할 수 있습니다.");
		}

		booking.delete(customUser.getUserId());
	}

	@Override
	@Transactional(readOnly = true)
	public AdminBookingReadResponse adminReadBooking(CustomUser customUser, UUID id) {

		String role = customUser.getRole();
		if (role == null || (!role.equals("MANAGER") && !role.equals("MASTER"))) {
			throw new RuntimeException("접근 권한이 없습니다.");
		}

		Booking booking = bookingRepository.findById(id).orElseThrow(() -> new RuntimeException("존재하지 않는 예약입니다."));

		return AdminBookingReadResponse.toDto(booking);
	}

	@Override
	@Transactional(readOnly = true)
	public AdminBookingPageResponse adminReadBookings(CustomUser customUser, Pageable pageable) {

		String role = customUser.getRole();
		if (role == null || (!role.equals("MANAGER") && !role.equals("MASTER"))) {
			throw new RuntimeException("접근 권한이 없습니다.");
		}

		Page<Booking> page = bookingRepository.findAll(pageable);

		return AdminBookingPageResponse.toDto(page);
	}
}