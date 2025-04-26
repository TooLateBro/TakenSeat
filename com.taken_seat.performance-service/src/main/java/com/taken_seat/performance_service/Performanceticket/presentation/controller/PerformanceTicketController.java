package com.taken_seat.performance_service.Performanceticket.presentation.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taken_seat.common_service.dto.ApiResponseData;
import com.taken_seat.common_service.dto.request.TicketPerformanceClientRequest;
import com.taken_seat.common_service.dto.response.TicketPerformanceClientResponse;
import com.taken_seat.performance_service.Performanceticket.application.dto.mapper.RequestMapper;
import com.taken_seat.performance_service.Performanceticket.application.service.PerformanceTicketService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/performancetickets")
public class PerformanceTicketController {

	private final PerformanceTicketService performanceTicketService;

	@GetMapping("/{performanceId}/schedules/{performanceScheduleId}/schedule-seats/{scheduleSeatId}")
	public ResponseEntity<ApiResponseData<TicketPerformanceClientResponse>> getPerformanceInfo(
		@PathVariable UUID performanceId,
		@PathVariable UUID performanceScheduleId,
		@PathVariable UUID scheduleSeatId) {

		TicketPerformanceClientRequest request = RequestMapper.fromParams
			(performanceId, performanceScheduleId, scheduleSeatId);

		TicketPerformanceClientResponse response = performanceTicketService.getPerformanceInfo(request);
		return ResponseEntity.ok(ApiResponseData.success(response));
	}
}
