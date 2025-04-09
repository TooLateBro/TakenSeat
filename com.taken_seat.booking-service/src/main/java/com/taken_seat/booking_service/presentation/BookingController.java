package com.taken_seat.booking_service.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taken_seat.booking_service.application.BookingService;
import com.taken_seat.booking_service.application.dto.request.BookingCreateRequest;
import com.taken_seat.booking_service.application.dto.response.BookingCreateResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bookings")
public class BookingController {

	private final BookingService bookingService;

	@PostMapping
	public ResponseEntity<BookingCreateResponse> createBooking(@RequestBody @Valid BookingCreateRequest request) {
		BookingCreateResponse response = bookingService.createBooking(request);

		return ResponseEntity.ok(response);
	}
}