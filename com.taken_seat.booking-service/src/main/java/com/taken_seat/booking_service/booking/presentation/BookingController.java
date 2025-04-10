package com.taken_seat.booking_service.booking.presentation;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taken_seat.booking_service.booking.application.BookingService;
import com.taken_seat.booking_service.booking.application.dto.request.BookingCreateRequest;
import com.taken_seat.booking_service.booking.application.dto.response.BookingCreateResponse;
import com.taken_seat.booking_service.booking.application.dto.response.BookingPageResponse;
import com.taken_seat.booking_service.booking.application.dto.response.BookingReadResponse;
import com.taken_seat.common_service.dto.CustomUser;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bookings")
public class BookingController {

	private final BookingService bookingService;

	@PostMapping
	public ResponseEntity<BookingCreateResponse> createBooking(CustomUser customUser,
		@RequestBody @Valid BookingCreateRequest request) {
		BookingCreateResponse response = bookingService.createBooking(customUser, request);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/{id}")
	public ResponseEntity<BookingReadResponse> readBooking(CustomUser customUser, @PathVariable("id") UUID id) {
		BookingReadResponse response = bookingService.readBooking(customUser, id);

		return ResponseEntity.ok(response);
	}

	@GetMapping
	public ResponseEntity<BookingPageResponse> readBookings(CustomUser customUser, Pageable pageable) {
		BookingPageResponse response = bookingService.readBookings(customUser, pageable);

		return ResponseEntity.ok(response);
	}

	@PatchMapping("/{id}")
	public ResponseEntity<Void> updateBooking(CustomUser customUser, @PathVariable("id") UUID id) {
		bookingService.updateBooking(customUser, id);

		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteBooking(CustomUser customUser, @PathVariable("id") UUID id) {
		bookingService.deleteBooking(customUser, id);

		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}