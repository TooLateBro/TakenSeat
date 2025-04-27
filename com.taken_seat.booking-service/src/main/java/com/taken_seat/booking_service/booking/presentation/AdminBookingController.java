package com.taken_seat.booking_service.booking.presentation;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.taken_seat.booking_service.booking.application.dto.command.BookingAdminPageReadCommand;
import com.taken_seat.booking_service.booking.application.dto.command.BookingSingleTargetCommand;
import com.taken_seat.booking_service.booking.application.dto.mapper.BookingCommandMapper;
import com.taken_seat.booking_service.booking.application.service.BookingService;
import com.taken_seat.booking_service.booking.presentation.dto.response.AdminBookingPageResponse;
import com.taken_seat.booking_service.booking.presentation.dto.response.AdminBookingReadResponse;
import com.taken_seat.common_service.dto.ApiResponseData;
import com.taken_seat.common_service.dto.AuthenticatedUser;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/bookings")
public class AdminBookingController {

	private final BookingService bookingService;
	private final BookingCommandMapper commandMapper;

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponseData<AdminBookingReadResponse>> readBooking(AuthenticatedUser authenticatedUser,
		@PathVariable("id") UUID id) {

		BookingSingleTargetCommand command = commandMapper.toCommand(authenticatedUser, id);
		AdminBookingReadResponse response = bookingService.adminReadBooking(command);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponseData.success(response));
	}

	@GetMapping
	public ResponseEntity<ApiResponseData<AdminBookingPageResponse>> readBookings(AuthenticatedUser authenticatedUser,
		@RequestParam(value = "userId") UUID userId, Pageable pageable) {

		BookingAdminPageReadCommand command = commandMapper.toCommand(authenticatedUser, userId, pageable);
		AdminBookingPageResponse response = bookingService.adminReadBookings(command);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponseData.success(response));
	}
}