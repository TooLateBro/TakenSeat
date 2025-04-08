package com.taken_seat.performance_service.performance.presentation.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taken_seat.performance_service.performance.application.dto.request.CreateRequestDto;
import com.taken_seat.performance_service.performance.application.dto.response.CreateResponseDto;
import com.taken_seat.performance_service.performance.application.service.PerformanceService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/performances")
public class PerformanceController {

	private final PerformanceService performanceService;

	/**
	 * 공연 생성 API
	 * 권한: ADMIN, MANAGER, PRODUCER
	 */
	@PostMapping
	public ResponseEntity<CreateResponseDto> create(
		@Valid @RequestBody CreateRequestDto request) {

		CreateResponseDto response = performanceService.create(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);

	}
}
