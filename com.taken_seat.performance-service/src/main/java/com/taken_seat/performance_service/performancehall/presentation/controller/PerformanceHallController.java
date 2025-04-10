package com.taken_seat.performance_service.performancehall.presentation.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taken_seat.performance_service.performancehall.application.dto.request.CreateRequestDto;
import com.taken_seat.performance_service.performancehall.application.dto.response.CreateResponseDto;
import com.taken_seat.performance_service.performancehall.application.service.PerformanceHallService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/performancehalls")
public class PerformanceHallController {

	private final PerformanceHallService performanceHallService;

	@PostMapping
	public ResponseEntity<CreateResponseDto> create(@Valid @RequestBody CreateRequestDto request) {

		CreateResponseDto response = performanceHallService.create(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
}
