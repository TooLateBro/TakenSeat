package com.taken_seat.booking_service.booking.infrastructure.service;

import java.util.List;
import java.util.UUID;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taken_seat.booking_service.booking.application.dto.query.BookingAdminListQuery;
import com.taken_seat.booking_service.booking.application.dto.query.BookingListQuery;
import com.taken_seat.booking_service.booking.application.dto.query.BookingReadQuery;
import com.taken_seat.booking_service.booking.application.service.BookingClientService;
import com.taken_seat.booking_service.booking.application.service.BookingQueryService;
import com.taken_seat.booking_service.booking.domain.Booking;
import com.taken_seat.booking_service.booking.domain.repository.BookingAdminRepository;
import com.taken_seat.booking_service.booking.domain.repository.BookingRepository;
import com.taken_seat.booking_service.booking.presentation.dto.response.AdminBookingPageResponse;
import com.taken_seat.booking_service.booking.presentation.dto.response.AdminBookingReadResponse;
import com.taken_seat.booking_service.booking.presentation.dto.response.BookingPageResponse;
import com.taken_seat.booking_service.booking.presentation.dto.response.BookingReadResponse;
import com.taken_seat.common_service.dto.response.TicketPerformanceClientResponse;
import com.taken_seat.common_service.exception.customException.BookingException;
import com.taken_seat.common_service.exception.enums.ResponseCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingQueryServiceImpl implements BookingQueryService {

	private final BookingAdminRepository bookingAdminRepository;
	private final BookingClientService bookingClientService;
	private final BookingRepository bookingRepository;

	@Override
	@Transactional(readOnly = true)
	@Cacheable(value = "readBooking", key = "#query.bookingId()")
	public BookingReadResponse readBooking(BookingReadQuery query) {

		log.info("[Booking] 조회 - 시도: | userId={}", query.userId());

		Booking booking = findBookingByIdAndUserId(query.bookingId(), query.userId());
		TicketPerformanceClientResponse response = bookingClientService.getPerformanceInfo(
			booking.getPerformanceId(), booking.getPerformanceScheduleId(), booking.getScheduleSeatId());

		log.info("[Booking] 조회 - 성공: | userId={}", query.userId());

		return BookingReadResponse.toDto(booking, response);
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(value = "readBookings", key = "#query.userId() + ':' + #query.pageable().pageNumber + ':' + #query.pageable().pageSize + ':' + #query.pageable().sort")
	public BookingPageResponse readBookings(BookingListQuery query) {

		log.info("[Booking] 조회 - 시도: | userId={}", query.userId());

		Page<Booking> page = bookingRepository.findAllByUserId(query.pageable(), query.userId());
		List<TicketPerformanceClientResponse> responses = page.getContent().stream()
			.map(e -> bookingClientService.getPerformanceInfo(
				e.getPerformanceId(),
				e.getPerformanceScheduleId(),
				e.getScheduleSeatId()
			))
			.toList();

		log.info("[Booking] 조회 - 성공: | userId={}", query.userId());

		return BookingPageResponse.toDto(page, responses);
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(value = "adminReadBooking", key = "#query.bookingId()")
	public AdminBookingReadResponse adminReadBooking(BookingReadQuery query) {

		log.info("[Booking] 관리자 예매 조회 - 시도: | userId={}", query.userId());

		String role = query.role();
		if (role == null || (!role.equals("MANAGER") && !role.equals("MASTER"))) {
			log.warn(
				"[Booking] 관리자 예매 조회 - 실패: {} | userId={}",
				ResponseCode.ACCESS_DENIED_EXCEPTION.getMessage(),
				query.userId()
			);
			throw new BookingException(ResponseCode.ACCESS_DENIED_EXCEPTION);
		}

		Booking booking = bookingAdminRepository.findById(query.bookingId())
			.orElseThrow(() -> new BookingException(ResponseCode.BOOKING_NOT_FOUND_EXCEPTION));
		log.info("[Booking] 관리자 예매 조회 - 성공: | userId={}", query.userId());

		return AdminBookingReadResponse.toDto(booking);
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(value = "adminReadBookings", key = "#query.queryUserId() + ':' + #query.pageable().pageNumber + ':' + #query.pageable().pageSize + ':' + #query.pageable().sort")
	public AdminBookingPageResponse adminReadBookings(BookingAdminListQuery query) {

		log.info("[Booking] 관리자 예매 조회 - 시도: | userId={}", query.userId());

		String role = query.role();
		if (role == null || (!role.equals("MANAGER") && !role.equals("MASTER"))) {
			log.warn(
				"[Booking] 관리자 예매 조회 - 실패: {} | userId={}",
				ResponseCode.ACCESS_DENIED_EXCEPTION.getMessage(),
				query.userId()
			);
			throw new BookingException(ResponseCode.ACCESS_DENIED_EXCEPTION);
		}

		if (query.queryUserId() == null) {
			log.warn(
				"[Booking] 관리자 예매 조회 - 실패: {} | userId={}",
				ResponseCode.BOOKING_QUERY_MISSING_EXCEPTION.getMessage(),
				query.userId()
			);
			throw new BookingException(ResponseCode.BOOKING_QUERY_MISSING_EXCEPTION);
		}

		Page<Booking> page = bookingAdminRepository.findAll(query.pageable());
		log.info("[Booking] 관리자 예매 조회 - 성공: | userId={}", query.userId());

		return AdminBookingPageResponse.toDto(page);
	}

	private Booking findBookingByIdAndUserId(UUID id, UUID userId) {
		return bookingRepository.findByIdAndUserId(id, userId)
			.orElseThrow(() -> new BookingException(ResponseCode.BOOKING_NOT_FOUND_EXCEPTION));
	}
}