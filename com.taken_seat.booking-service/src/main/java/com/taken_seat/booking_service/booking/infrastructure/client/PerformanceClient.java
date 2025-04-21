package com.taken_seat.booking_service.booking.infrastructure.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.taken_seat.common_service.dto.ApiResponseData;
import com.taken_seat.common_service.dto.request.BookingSeatClientRequestDto;
import com.taken_seat.common_service.dto.response.BookingSeatClientResponseDto;
import com.taken_seat.common_service.dto.response.PerformanceStartTimeDto;

@FeignClient(name = "performance-service", path = "/api/v1", contextId = "booking")
public interface PerformanceClient {

	@PutMapping("/performancehalls/seat/status")
	ApiResponseData<BookingSeatClientResponseDto> updateSeatStatus(@RequestBody BookingSeatClientRequestDto requestDto);

	@PutMapping("/performancehalls/seat/status/cancel")
	ApiResponseData<BookingSeatClientResponseDto> cancelSeatStatus(@RequestBody BookingSeatClientRequestDto requestDto);

	@GetMapping("/performances/{performanceId}/schedules/{performanceScheduleId}/start-time")
	ApiResponseData<PerformanceStartTimeDto> getPerformanceStartTime(@PathVariable("performanceId") UUID performanceId,
		@PathVariable("performanceScheduleId") UUID performanceScheduleId);
}