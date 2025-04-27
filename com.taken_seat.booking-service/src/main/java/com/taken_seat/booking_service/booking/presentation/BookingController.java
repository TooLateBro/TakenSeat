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

import com.taken_seat.booking_service.booking.application.dto.command.BookingCreateCommand;
import com.taken_seat.booking_service.booking.application.dto.command.BookingPageReadCommand;
import com.taken_seat.booking_service.booking.application.dto.command.BookingPaymentCommand;
import com.taken_seat.booking_service.booking.application.dto.command.BookingSingleTargetCommand;
import com.taken_seat.booking_service.booking.application.dto.mapper.BookingCommandMapper;
import com.taken_seat.booking_service.booking.application.service.BookingService;
import com.taken_seat.booking_service.booking.presentation.dto.request.BookingCreateRequest;
import com.taken_seat.booking_service.booking.presentation.dto.request.BookingPayRequest;
import com.taken_seat.booking_service.booking.presentation.dto.response.BookingCreateResponse;
import com.taken_seat.booking_service.booking.presentation.dto.response.BookingPageResponse;
import com.taken_seat.booking_service.booking.presentation.dto.response.BookingReadResponse;
import com.taken_seat.common_service.dto.ApiResponseData;
import com.taken_seat.common_service.dto.AuthenticatedUser;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bookings")
public class BookingController {

	private final BookingService bookingService;
	private final BookingCommandMapper commandMapper;

	@PostMapping
	public ResponseEntity<ApiResponseData<BookingCreateResponse>> createBooking(AuthenticatedUser authenticatedUser,
		@RequestBody @Valid BookingCreateRequest request) {

		BookingCreateCommand command = commandMapper.toCommand(authenticatedUser, request);
		BookingCreateResponse response = bookingService.createBooking(command);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponseData.success(response));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponseData<BookingReadResponse>> readBooking(AuthenticatedUser authenticatedUser,
		@PathVariable("id") UUID id) {

		BookingSingleTargetCommand command = commandMapper.toCommand(authenticatedUser, id);
		BookingReadResponse response = bookingService.readBooking(command);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponseData.success(response));
	}

	@GetMapping
	public ResponseEntity<ApiResponseData<BookingPageResponse>> readBookings(AuthenticatedUser authenticatedUser,
		Pageable pageable) {

		BookingPageReadCommand command = commandMapper.toCommand(authenticatedUser, pageable);
		BookingPageResponse response = bookingService.readBookings(command);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponseData.success(response));
	}

	@PatchMapping("/{id}")
	public ResponseEntity<ApiResponseData<Void>> cancelBooking(AuthenticatedUser authenticatedUser,
		@PathVariable("id") UUID id) {

		BookingSingleTargetCommand command = commandMapper.toCommand(authenticatedUser, id);
		bookingService.cancelBooking(command);

		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponseData.success());
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponseData<Void>> deleteBooking(AuthenticatedUser authenticatedUser,
		@PathVariable("id") UUID id) {

		BookingSingleTargetCommand command = commandMapper.toCommand(authenticatedUser, id);
		bookingService.deleteBooking(command);

		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponseData.success());
	}

	@PostMapping("/{id}/payment")
	public ResponseEntity<ApiResponseData<Void>> createPayment(AuthenticatedUser authenticatedUser,
		@PathVariable("id") UUID id,
		@RequestBody BookingPayRequest request) {

		BookingPaymentCommand command = commandMapper.toCommand(authenticatedUser, id, request);
		bookingService.createPayment(command);

		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponseData.success());
	}
}