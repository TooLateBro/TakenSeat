package com.taken_seat.booking_service.booking.presentation;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taken_seat.booking_service.booking.application.BookingClientService;
import com.taken_seat.common_service.dto.ApiResponseData;
import com.taken_seat.common_service.dto.response.BookingStatusDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bookings")
public class BookingClientController {

	private final BookingClientService bookingClientService;

	@GetMapping("/{userId}/{performanceId}/status")
	public ResponseEntity<ApiResponseData<BookingStatusDto>> getBookingStatus(@PathVariable("userId") UUID userId,
		@PathVariable("performanceId") UUID performanceId) {

		BookingStatusDto response = bookingClientService.getBookingStatus(userId, performanceId);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponseData.success(response));
	}
}