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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.taken_seat.booking_service.booking.application.dto.command.BookingCreateCommand;
import com.taken_seat.booking_service.booking.application.dto.command.BookingPaymentCommand;
import com.taken_seat.booking_service.booking.application.dto.command.BookingSingleTargetCommand;
import com.taken_seat.booking_service.booking.application.dto.mapper.BookingMapper;
import com.taken_seat.booking_service.booking.application.dto.query.BookingAdminListQuery;
import com.taken_seat.booking_service.booking.application.dto.query.BookingListQuery;
import com.taken_seat.booking_service.booking.application.dto.query.BookingReadQuery;
import com.taken_seat.booking_service.booking.application.dto.query.BookingStatusQuery;
import com.taken_seat.booking_service.booking.application.service.BookingService;
import com.taken_seat.booking_service.booking.presentation.dto.request.BookingCreateRequest;
import com.taken_seat.booking_service.booking.presentation.dto.request.BookingPayRequest;
import com.taken_seat.booking_service.booking.presentation.dto.response.AdminBookingPageResponse;
import com.taken_seat.booking_service.booking.presentation.dto.response.AdminBookingReadResponse;
import com.taken_seat.booking_service.booking.presentation.dto.response.BookingCreateResponse;
import com.taken_seat.booking_service.booking.presentation.dto.response.BookingPageResponse;
import com.taken_seat.booking_service.booking.presentation.dto.response.BookingReadResponse;
import com.taken_seat.common_service.aop.annotation.RoleCheck;
import com.taken_seat.common_service.aop.vo.Role;
import com.taken_seat.common_service.dto.ApiResponseData;
import com.taken_seat.common_service.dto.AuthenticatedUser;
import com.taken_seat.common_service.dto.response.BookingStatusDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class BookingController {

	private final BookingService bookingService;
	private final BookingMapper bookingMapper;

	@PostMapping("/api/v1/bookings")
	public ResponseEntity<ApiResponseData<BookingCreateResponse>> createBooking(AuthenticatedUser authenticatedUser,
		@RequestBody @Valid BookingCreateRequest request) {

		BookingCreateCommand command = bookingMapper.toCommand(authenticatedUser, request);
		BookingCreateResponse response = bookingService.createBooking(command);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponseData.success(response));
	}

	@PatchMapping("/api/v1/bookings/{id}")
	public ResponseEntity<ApiResponseData<Void>> cancelBooking(AuthenticatedUser authenticatedUser,
		@PathVariable("id") UUID id) {

		BookingSingleTargetCommand command = bookingMapper.toCommand(authenticatedUser, id);
		bookingService.cancelBooking(command);

		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponseData.success());
	}

	@DeleteMapping("/api/v1/bookings/{id}")
	public ResponseEntity<ApiResponseData<Void>> deleteBooking(AuthenticatedUser authenticatedUser,
		@PathVariable("id") UUID id) {

		BookingSingleTargetCommand command = bookingMapper.toCommand(authenticatedUser, id);
		bookingService.deleteBooking(command);

		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponseData.success());
	}

	@PostMapping("/api/v1/bookings/{id}/payment")
	public ResponseEntity<ApiResponseData<Void>> createPayment(AuthenticatedUser authenticatedUser,
		@PathVariable("id") UUID id,
		@RequestBody(required = false) BookingPayRequest request) {

		BookingPaymentCommand command = bookingMapper.toCommand(authenticatedUser, id, request);
		bookingService.createPayment(command);

		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponseData.success());
	}

	@GetMapping("/api/v1/bookings/{id}")
	public ResponseEntity<ApiResponseData<BookingReadResponse>> readBooking(AuthenticatedUser authenticatedUser,
		@PathVariable("id") UUID id) {

		BookingReadQuery query = bookingMapper.toQuery(authenticatedUser, id);
		BookingReadResponse response = bookingService.readBooking(query);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponseData.success(response));
	}

	@GetMapping("/api/v1/bookings")
	public ResponseEntity<ApiResponseData<BookingPageResponse>> readBookings(AuthenticatedUser authenticatedUser,
		Pageable pageable) {

		BookingListQuery query = bookingMapper.toQuery(authenticatedUser, pageable);
		BookingPageResponse response = bookingService.readBookings(query);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponseData.success(response));
	}

	@GetMapping("/api/v1/bookings/{userId}/{performanceId}/status")
	public ResponseEntity<ApiResponseData<BookingStatusDto>> getBookingStatus(@PathVariable("userId") UUID userId,
		@PathVariable("performanceId") UUID performanceId) {

		BookingStatusQuery query = bookingMapper.toQuery(userId, performanceId);
		BookingStatusDto response = bookingService.getBookingStatus(query);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponseData.success(response));
	}

	@RoleCheck(allowedRoles = {Role.ADMIN, Role.MANAGER})
	@GetMapping("/api/v1/admin/bookings/{id}")
	public ResponseEntity<ApiResponseData<AdminBookingReadResponse>> adminReadBooking(
		AuthenticatedUser authenticatedUser, @PathVariable("id") UUID id) {

		BookingReadQuery query = bookingMapper.toQuery(authenticatedUser, id);
		AdminBookingReadResponse response = bookingService.adminReadBooking(query);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponseData.success(response));
	}

	@RoleCheck(allowedRoles = {Role.ADMIN, Role.MANAGER})
	@GetMapping("/api/v1/admin/bookings")
	public ResponseEntity<ApiResponseData<AdminBookingPageResponse>> adminReadBookings(
		AuthenticatedUser authenticatedUser, @RequestParam(value = "userId") UUID userId, Pageable pageable) {

		BookingAdminListQuery query = bookingMapper.toQuery(authenticatedUser, userId, pageable);
		AdminBookingPageResponse response = bookingService.adminReadBookings(query);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponseData.success(response));
	}

}