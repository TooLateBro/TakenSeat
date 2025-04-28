package com.taken_seat.performance_service.performance.presentation.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taken_seat.common_service.dto.ApiResponseData;
import com.taken_seat.common_service.dto.request.BookingSeatClientRequestDto;
import com.taken_seat.common_service.dto.response.BookingSeatClientResponseDto;
import com.taken_seat.common_service.dto.response.PerformanceEndTimeDto;
import com.taken_seat.common_service.dto.response.PerformanceStartTimeDto;
import com.taken_seat.performance_service.performance.application.service.PerformanceClientService;
import com.taken_seat.performance_service.performance.presentation.docs.PerformanceClientControllerDocs;
import com.taken_seat.performance_service.performance.presentation.dto.response.SeatLayoutResponseDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/performances")
public class PerformanceClientController implements PerformanceClientControllerDocs {

	private final PerformanceClientService performanceClientService;

	@PutMapping("/seat/status")
	public ResponseEntity<ApiResponseData<BookingSeatClientResponseDto>> updateSeatStatus(
		@Valid @RequestBody BookingSeatClientRequestDto request) {

		BookingSeatClientResponseDto response = performanceClientService.updateSeatStatus(request);
		return ResponseEntity.ok(ApiResponseData.success(response));
	}

	@PutMapping("/seat/status/cancel")
	public ResponseEntity<ApiResponseData<BookingSeatClientResponseDto>> cancelSeatStatus(
		@Valid @RequestBody BookingSeatClientRequestDto request) {

		BookingSeatClientResponseDto response = performanceClientService.cancelSeatStatus(request);
		return ResponseEntity.ok(ApiResponseData.success(response));
	}

	@GetMapping("/seats/{performanceScheduleId}")
	public ResponseEntity<ApiResponseData<SeatLayoutResponseDto>> getSeatLayout(
		@PathVariable("performanceScheduleId") UUID performanceScheduleId) {

		SeatLayoutResponseDto response = performanceClientService.getSeatLayout(performanceScheduleId);
		return ResponseEntity.ok(ApiResponseData.success(response));
	}

	@GetMapping("/{performanceId}/schedules/{performanceScheduleId}/end-time")
	public ResponseEntity<ApiResponseData<PerformanceEndTimeDto>> getPerformanceEndTime(
		@PathVariable("performanceId") UUID performanceId,
		@PathVariable("performanceScheduleId") UUID performanceScheduleId) {

		PerformanceEndTimeDto response = performanceClientService.getPerformanceEndTime(performanceId,
			performanceScheduleId);
		return ResponseEntity.ok(ApiResponseData.success(response));
	}

	@GetMapping("/{performanceId}/schedules/{performanceScheduleId}/start-time")
	public ResponseEntity<ApiResponseData<PerformanceStartTimeDto>> getPerformanceStartTime(
		@PathVariable("performanceId") UUID performanceId,
		@PathVariable("performanceScheduleId") UUID performanceScheduleId) {

		PerformanceStartTimeDto response = performanceClientService.getPerformanceStartTime(performanceId,
			performanceScheduleId);
		return ResponseEntity.ok(ApiResponseData.success(response));
	}
}
