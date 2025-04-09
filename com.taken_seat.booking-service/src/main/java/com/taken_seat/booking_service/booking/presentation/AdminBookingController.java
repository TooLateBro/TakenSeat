package com.taken_seat.booking_service.booking.presentation;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taken_seat.booking_service.booking.application.BookingService;
import com.taken_seat.booking_service.booking.application.dto.response.AdminBookingPageResponse;
import com.taken_seat.booking_service.booking.application.dto.response.AdminBookingReadResponse;
import com.taken_seat.booking_service.common.CustomUser;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/bookings")
public class AdminBookingController {

	private final BookingService bookingService;

	@GetMapping("/{id}")
	public ResponseEntity<AdminBookingReadResponse> readBooking(CustomUser customUser, @PathVariable("id") UUID id) {

		AdminBookingReadResponse response = bookingService.adminReadBooking(customUser, id);

		return ResponseEntity.ok(response);
	}

	@GetMapping
	public ResponseEntity<AdminBookingPageResponse> readBookings(CustomUser customUser, Pageable pageable) {

		AdminBookingPageResponse response = bookingService.adminReadBookings(customUser, pageable);

		return ResponseEntity.ok(response);
	}
}