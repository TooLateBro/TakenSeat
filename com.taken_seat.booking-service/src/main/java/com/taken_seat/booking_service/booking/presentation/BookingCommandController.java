package com.taken_seat.booking_service.booking.presentation;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taken_seat.booking_service.booking.application.dto.command.BookingCreateCommand;
import com.taken_seat.booking_service.booking.application.dto.command.BookingPaymentCommand;
import com.taken_seat.booking_service.booking.application.dto.command.BookingSingleTargetCommand;
import com.taken_seat.booking_service.booking.application.dto.mapper.BookingMapper;
import com.taken_seat.booking_service.booking.application.service.BookingCommandService;
import com.taken_seat.booking_service.booking.presentation.dto.request.BookingCreateRequest;
import com.taken_seat.booking_service.booking.presentation.dto.request.BookingPayRequest;
import com.taken_seat.booking_service.booking.presentation.dto.response.BookingCreateResponse;
import com.taken_seat.common_service.dto.ApiResponseData;
import com.taken_seat.common_service.dto.AuthenticatedUser;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bookings")
public class BookingCommandController {

	private final BookingCommandService bookingCommandService;
	private final BookingMapper commandMapper;

	@PostMapping
	public ResponseEntity<ApiResponseData<BookingCreateResponse>> createBooking(AuthenticatedUser authenticatedUser,
		@RequestBody @Valid BookingCreateRequest request) {

		BookingCreateCommand command = commandMapper.toCommand(authenticatedUser, request);
		BookingCreateResponse response = bookingCommandService.createBooking(command);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponseData.success(response));
	}

	@PatchMapping("/{id}")
	public ResponseEntity<ApiResponseData<Void>> cancelBooking(AuthenticatedUser authenticatedUser,
		@PathVariable("id") UUID id) {

		BookingSingleTargetCommand command = commandMapper.toCommand(authenticatedUser, id);
		bookingCommandService.cancelBooking(command);

		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponseData.success());
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponseData<Void>> deleteBooking(AuthenticatedUser authenticatedUser,
		@PathVariable("id") UUID id) {

		BookingSingleTargetCommand command = commandMapper.toCommand(authenticatedUser, id);
		bookingCommandService.deleteBooking(command);

		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponseData.success());
	}

	@PostMapping("/{id}/payment")
	public ResponseEntity<ApiResponseData<Void>> createPayment(AuthenticatedUser authenticatedUser,
		@PathVariable("id") UUID id,
		@RequestBody(required = false) BookingPayRequest request) {

		BookingPaymentCommand command = commandMapper.toCommand(authenticatedUser, id, request);
		bookingCommandService.createPayment(command);

		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponseData.success());
	}
}