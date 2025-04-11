package com.taken_seat.performance_service.performenceticket.presentation.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taken_seat.common_service.dto.request.TicketPerformanceClientRequest;
import com.taken_seat.common_service.dto.response.TicketPerformanceClientResponse;
import com.taken_seat.performance_service.performenceticket.application.dto.mapper.RequestMapper;
import com.taken_seat.performance_service.performenceticket.application.service.PerformenceTicketService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/performencetickets")
public class PerformenceTicketController {

	private final PerformenceTicketService performenceTicketService;

	@GetMapping("/{performanceId}/{performanceScheduleId}/seats/{seatId}")
	public ResponseEntity<TicketPerformanceClientResponse> getPerformanceInfo(
		@PathVariable UUID performanceId,
		@PathVariable UUID performanceScheduleId,
		@PathVariable UUID seatId) {

		TicketPerformanceClientRequest request = RequestMapper.fromParams(performanceId, performanceScheduleId, seatId);

		TicketPerformanceClientResponse response = performenceTicketService.getPerformanceInfo(request);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
}
