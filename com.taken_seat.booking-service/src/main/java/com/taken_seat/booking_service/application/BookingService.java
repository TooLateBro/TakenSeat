package com.taken_seat.booking_service.application;

import com.taken_seat.booking_service.application.dto.request.BookingCreateRequest;
import com.taken_seat.booking_service.application.dto.response.BookingCreateResponse;

public interface BookingService {
	BookingCreateResponse createBooking(BookingCreateRequest request);
}