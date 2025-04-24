package com.taken_seat.performance_service.performancehall.presentation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taken_seat.common_service.dto.ApiResponseData;
import com.taken_seat.common_service.dto.request.BookingSeatClientRequestDto;
import com.taken_seat.common_service.dto.response.BookingSeatClientResponseDto;
import com.taken_seat.performance_service.performancehall.application.service.PerformanceHallRedissonService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/seats")
@RequiredArgsConstructor
public class SeatRedissonController {

	private final PerformanceHallRedissonService performanceHallRedissonService;

	@PutMapping("/lock")
	public ResponseEntity<ApiResponseData<BookingSeatClientResponseDto>> updateSeatStatusWithLock(
		@RequestBody BookingSeatClientRequestDto request) {

		BookingSeatClientResponseDto response = performanceHallRedissonService.updateSeatStatusWithLock(request);
		return ResponseEntity.ok(ApiResponseData.success(response));
	}

	@PutMapping("/lock/cancel")
	public ResponseEntity<ApiResponseData<BookingSeatClientResponseDto>> updateSeatStatusCancelWithLock(
		@RequestBody BookingSeatClientRequestDto request) {

		BookingSeatClientResponseDto response = performanceHallRedissonService.updateSeatStatusCancelWithLock(request);
		return ResponseEntity.ok(ApiResponseData.success(response));
	}
}
