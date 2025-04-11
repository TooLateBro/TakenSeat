package com.taken_seat.booking_service.booking.infrastructure.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taken_seat.booking_service.booking.application.dto.request.BookingCreateRequest;
import com.taken_seat.booking_service.booking.application.dto.response.AdminBookingPageResponse;
import com.taken_seat.booking_service.booking.application.dto.response.AdminBookingReadResponse;
import com.taken_seat.booking_service.booking.application.dto.response.BookingCreateResponse;
import com.taken_seat.booking_service.booking.application.dto.response.BookingPageResponse;
import com.taken_seat.booking_service.booking.application.dto.response.BookingReadResponse;
import com.taken_seat.booking_service.booking.application.service.BookingEventProducer;
import com.taken_seat.booking_service.booking.application.service.BookingService;
import com.taken_seat.booking_service.booking.application.service.RedissonService;
import com.taken_seat.booking_service.booking.domain.Booking;
import com.taken_seat.booking_service.booking.domain.repository.BookingAdminRepository;
import com.taken_seat.booking_service.booking.domain.repository.BookingRepository;
import com.taken_seat.common_service.dto.AuthenticatedUser;
import com.taken_seat.common_service.exception.customException.BookingException;
import com.taken_seat.common_service.exception.enums.ResponseCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

	private final RedissonService redissonService;
	private final BookingRepository bookingRepository;
	private final BookingAdminRepository bookingAdminRepository;
	private final BookingEventProducer bookingEventProducer;

	@Override
	@Transactional
	public BookingCreateResponse createBooking(AuthenticatedUser authenticatedUser, BookingCreateRequest request) {

		Booking booking = Booking.builder()
			.userId(authenticatedUser.getUserId())
			.performanceId(request.getPerformanceId())
			.performanceScheduleId(request.getPerformanceScheduleId())
			.seatId(request.getSeatId())
			.build();

		redissonService.tryHoldSeat(request.getPerformanceId(), request.getPerformanceScheduleId(),
			request.getSeatId());

		Booking saved = bookingRepository.save(booking);
		bookingEventProducer.sendBookingCreatedEvent(saved, 10000);

		return BookingCreateResponse.toDto(saved);
	}

	@Override
	@Transactional(readOnly = true)
	public BookingReadResponse readBooking(AuthenticatedUser authenticatedUser, UUID id) {

		Booking booking = findBookingByIdAndUserId(id, authenticatedUser.getUserId());

		return BookingReadResponse.toDto(booking);
	}

	@Override
	@Transactional(readOnly = true)
	public BookingPageResponse readBookings(AuthenticatedUser authenticatedUser, Pageable pageable) {

		Page<Booking> page = bookingRepository.findAllByUserId(pageable, authenticatedUser.getUserId());

		return BookingPageResponse.toDto(page);
	}

	@Override
	@Transactional
	public void updateBooking(AuthenticatedUser authenticatedUser, UUID id) {

		Booking booking = findBookingByIdAndUserId(id, authenticatedUser.getUserId());

		if (booking.getCanceledAt() != null) {
			throw new BookingException(ResponseCode.BOOKING_ALREADY_CANCELED_EXCEPTION);
		}

		booking.cancel();

		if (booking.getPaymentId() != null) {
			// TODO: 환불 요청 보내기
		}

		// TODO: 좌석 선점 해제 요청 보내기
	}

	@Override
	@Transactional
	public void deleteBooking(AuthenticatedUser authenticatedUser, UUID id) {

		Booking booking = findBookingByIdAndUserId(id, authenticatedUser.getUserId());

		if (booking.getCanceledAt() == null) {
			throw new BookingException(ResponseCode.BOOKING_NOT_CANCELED_EXCEPTION);
		}

		booking.delete(authenticatedUser.getUserId());
	}

	@Override
	@Transactional(readOnly = true)
	public AdminBookingReadResponse adminReadBooking(AuthenticatedUser authenticatedUser, UUID id) {

		String role = authenticatedUser.getRole();
		if (role == null || (!role.equals("MANAGER") && !role.equals("MASTER"))) {
			throw new BookingException(ResponseCode.ACCESS_DENIED_EXCEPTION);
		}

		Booking booking = bookingAdminRepository.findById(id)
			.orElseThrow(() -> new BookingException(ResponseCode.BOOKING_NOT_FOUND_EXCEPTION));

		return AdminBookingReadResponse.toDto(booking);
	}

	@Override
	@Transactional(readOnly = true)
	public AdminBookingPageResponse adminReadBookings(AuthenticatedUser authenticatedUser, Pageable pageable) {

		// TODO: Querydsl 을 적용하여 사용자ID 포함 동적 검색 적용하기
		String role = authenticatedUser.getRole();
		if (role == null || (!role.equals("MANAGER") && !role.equals("MASTER"))) {
			throw new BookingException(ResponseCode.ACCESS_DENIED_EXCEPTION);
		}

		Page<Booking> page = bookingAdminRepository.findAll(pageable);

		return AdminBookingPageResponse.toDto(page);
	}

	private Booking findBookingByIdAndUserId(UUID id, UUID userId) {
		return bookingRepository.findByIdAndUserId(id, userId)
			.orElseThrow(() -> new BookingException(ResponseCode.BOOKING_NOT_FOUND_EXCEPTION));
	}
}