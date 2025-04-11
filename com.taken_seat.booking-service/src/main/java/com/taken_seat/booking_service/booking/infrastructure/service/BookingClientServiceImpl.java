package com.taken_seat.booking_service.booking.infrastructure.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.taken_seat.booking_service.booking.application.service.BookingClientService;
import com.taken_seat.booking_service.booking.domain.Booking;
import com.taken_seat.booking_service.booking.domain.repository.BookingRepository;
import com.taken_seat.common_service.dto.response.BookingStatusDto;
import com.taken_seat.common_service.exception.customException.BookingException;
import com.taken_seat.common_service.exception.enums.ResponseCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingClientServiceImpl implements BookingClientService {

	private final BookingRepository bookingRepository;

	@Override
	public BookingStatusDto getBookingStatus(UUID userId, UUID performanceId) {
		Booking booking = bookingRepository.findByUserIdAndPerformanceId(userId, performanceId)
			.orElseThrow(() -> new BookingException(ResponseCode.BOOKING_NOT_FOUND_EXCEPTION));

		return new BookingStatusDto(booking.getBookingStatus().name());
	}
}