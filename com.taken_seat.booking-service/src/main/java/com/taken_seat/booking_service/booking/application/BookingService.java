package com.taken_seat.booking_service.booking.application;

import java.util.UUID;

import org.springframework.data.domain.Pageable;

import com.taken_seat.booking_service.booking.application.dto.request.BookingCreateRequest;
import com.taken_seat.booking_service.booking.application.dto.response.AdminBookingPageResponse;
import com.taken_seat.booking_service.booking.application.dto.response.AdminBookingReadResponse;
import com.taken_seat.booking_service.booking.application.dto.response.BookingCreateResponse;
import com.taken_seat.booking_service.booking.application.dto.response.BookingPageResponse;
import com.taken_seat.booking_service.booking.application.dto.response.BookingReadResponse;
import com.taken_seat.booking_service.common.CustomUser;

public interface BookingService {
	BookingCreateResponse createBooking(CustomUser customUser, BookingCreateRequest request);

	BookingReadResponse readBooking(CustomUser customUser, UUID id);

	BookingPageResponse readBookings(CustomUser customUser, Pageable pageable);

	void updateBooking(CustomUser customUser, UUID id);

	void deleteBooking(CustomUser customUser, UUID id);

	AdminBookingReadResponse adminReadBooking(CustomUser customUser, UUID id);

	AdminBookingPageResponse adminReadBookings(CustomUser customUser, Pageable pageable);
}