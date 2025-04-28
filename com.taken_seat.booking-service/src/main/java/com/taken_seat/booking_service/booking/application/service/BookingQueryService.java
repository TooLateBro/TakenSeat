package com.taken_seat.booking_service.booking.application.service;

import com.taken_seat.booking_service.booking.application.dto.query.BookingAdminListQuery;
import com.taken_seat.booking_service.booking.application.dto.query.BookingListQuery;
import com.taken_seat.booking_service.booking.application.dto.query.BookingReadQuery;
import com.taken_seat.booking_service.booking.presentation.dto.response.AdminBookingPageResponse;
import com.taken_seat.booking_service.booking.presentation.dto.response.AdminBookingReadResponse;
import com.taken_seat.booking_service.booking.presentation.dto.response.BookingPageResponse;
import com.taken_seat.booking_service.booking.presentation.dto.response.BookingReadResponse;

public interface BookingQueryService {
	BookingReadResponse readBooking(BookingReadQuery query);

	BookingPageResponse readBookings(BookingListQuery query);

	AdminBookingReadResponse adminReadBooking(BookingReadQuery query);

	AdminBookingPageResponse adminReadBookings(BookingAdminListQuery query);
}