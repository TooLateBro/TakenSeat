package com.taken_seat.booking_service.application;

import java.util.UUID;

import org.springframework.data.domain.Pageable;

import com.taken_seat.booking_service.application.dto.request.BookingCreateRequest;
import com.taken_seat.booking_service.application.dto.response.BookingCreateResponse;
import com.taken_seat.booking_service.application.dto.response.BookingPageResponse;
import com.taken_seat.booking_service.application.dto.response.BookingReadResponse;

public interface BookingService {
	BookingCreateResponse createBooking(BookingCreateRequest request);

	BookingReadResponse readBooking(UUID id);

	BookingPageResponse readBookings(Pageable pageable, UUID userId);

	void updateBooking(UUID id);
}