package com.taken_seat.booking_service.presentation;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.taken_seat.booking_service.application.BookingService;
import com.taken_seat.booking_service.application.dto.request.BookingCreateRequest;
import com.taken_seat.booking_service.application.dto.response.BookingCreateResponse;
import com.taken_seat.booking_service.application.dto.response.BookingPageResponse;
import com.taken_seat.booking_service.application.dto.response.BookingReadResponse;

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

	@GetMapping("/{id}")
	public ResponseEntity<BookingReadResponse> readBooking(@PathVariable("id") UUID id) {
		BookingReadResponse response = bookingService.readBooking(id);

		return ResponseEntity.ok(response);
	}

	@GetMapping
	public ResponseEntity<BookingPageResponse> readBookings(Pageable pageable, @RequestParam("userId") UUID userId) {
		BookingPageResponse response = bookingService.readBookings(pageable, userId);

		return ResponseEntity.ok(response);
	}

	@PatchMapping("/{id}")
	public ResponseEntity<Void> updateBooking(@PathVariable("id") UUID id) {
		bookingService.updateBooking(id);

		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteBooking(@PathVariable("id") UUID id) {
		bookingService.deleteBooking(id);

		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}