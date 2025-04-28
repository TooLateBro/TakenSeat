package com.taken_seat.booking_service.booking.presentation;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.taken_seat.booking_service.booking.application.dto.mapper.BookingMapper;
import com.taken_seat.booking_service.booking.application.dto.query.BookingAdminListQuery;
import com.taken_seat.booking_service.booking.application.dto.query.BookingListQuery;
import com.taken_seat.booking_service.booking.application.dto.query.BookingReadQuery;
import com.taken_seat.booking_service.booking.application.service.BookingQueryService;
import com.taken_seat.booking_service.booking.presentation.dto.response.AdminBookingPageResponse;
import com.taken_seat.booking_service.booking.presentation.dto.response.AdminBookingReadResponse;
import com.taken_seat.booking_service.booking.presentation.dto.response.BookingPageResponse;
import com.taken_seat.booking_service.booking.presentation.dto.response.BookingReadResponse;
import com.taken_seat.common_service.dto.ApiResponseData;
import com.taken_seat.common_service.dto.AuthenticatedUser;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class BookingQueryController {

	private final BookingQueryService bookingQueryService;
	private final BookingMapper commandMapper;

	@GetMapping("/api/v1/bookings/{id}")
	public ResponseEntity<ApiResponseData<BookingReadResponse>> readBooking(AuthenticatedUser authenticatedUser,
		@PathVariable("id") UUID id) {

		BookingReadQuery query = commandMapper.toQuery(authenticatedUser, id);
		BookingReadResponse response = bookingQueryService.readBooking(query);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponseData.success(response));
	}

	@GetMapping("/api/v1/bookings")
	public ResponseEntity<ApiResponseData<BookingPageResponse>> readBookings(AuthenticatedUser authenticatedUser,
		Pageable pageable) {

		BookingListQuery query = commandMapper.toQuery(authenticatedUser, pageable);
		BookingPageResponse response = bookingQueryService.readBookings(query);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponseData.success(response));
	}

	@GetMapping("/api/v1/admin/bookings/{id}")
	public ResponseEntity<ApiResponseData<AdminBookingReadResponse>> adminReadBooking(
		AuthenticatedUser authenticatedUser, @PathVariable("id") UUID id) {

		BookingReadQuery query = commandMapper.toQuery(authenticatedUser, id);
		AdminBookingReadResponse response = bookingQueryService.adminReadBooking(query);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponseData.success(response));
	}

	@GetMapping("/api/v1/admin/bookings")
	public ResponseEntity<ApiResponseData<AdminBookingPageResponse>> adminReadBookings(
		AuthenticatedUser authenticatedUser, @RequestParam(value = "userId") UUID userId, Pageable pageable) {

		BookingAdminListQuery query = commandMapper.toQuery(authenticatedUser, userId, pageable);
		AdminBookingPageResponse response = bookingQueryService.adminReadBookings(query);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponseData.success(response));
	}
}