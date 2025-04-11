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
import com.taken_seat.common_service.dto.ApiResponseData;
import com.taken_seat.common_service.dto.AuthenticatedUser;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bookings")
public class BookingController {

	private final BookingService bookingService;

	@PostMapping
	public ResponseEntity<ApiResponseData<BookingCreateResponse>> createBooking(AuthenticatedUser authenticatedUser,
		@RequestBody @Valid BookingCreateRequest request) {
		BookingCreateResponse response = bookingService.createBooking(authenticatedUser, request);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponseData.success(response));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponseData<BookingReadResponse>> readBooking(AuthenticatedUser authenticatedUser,
		@PathVariable("id") UUID id) {
		BookingReadResponse response = bookingService.readBooking(authenticatedUser, id);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponseData.success(response));
	}

	@GetMapping
	public ResponseEntity<ApiResponseData<BookingPageResponse>> readBookings(AuthenticatedUser authenticatedUser,
		Pageable pageable) {
		BookingPageResponse response = bookingService.readBookings(authenticatedUser, pageable);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponseData.success(response));
	}

	@PatchMapping("/{id}")
	public ResponseEntity<ApiResponseData<Void>> updateBooking(AuthenticatedUser authenticatedUser,
		@PathVariable("id") UUID id) {
		bookingService.updateBooking(authenticatedUser, id);

		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponseData.success());
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponseData<Void>> deleteBooking(AuthenticatedUser authenticatedUser,
		@PathVariable("id") UUID id) {
		bookingService.deleteBooking(authenticatedUser, id);

		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponseData.success());
	}
}