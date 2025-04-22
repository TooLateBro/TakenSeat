package com.taken_seat.booking_service.common.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.taken_seat.booking_service.booking.application.dto.response.SeatLayoutResponseDto;
import com.taken_seat.common_service.dto.ApiResponseData;
import com.taken_seat.common_service.dto.request.BookingSeatClientRequestDto;
import com.taken_seat.common_service.dto.response.BookingSeatClientResponseDto;
import com.taken_seat.common_service.dto.response.PerformanceStartTimeDto;
import com.taken_seat.common_service.dto.response.TicketPerformanceClientResponse;

@FeignClient(name = "performance-service", path = "/api/v1")
public interface PerformanceClient {

	@PutMapping("/performancehalls/seat/status")
	ApiResponseData<BookingSeatClientResponseDto> updateSeatStatus(@RequestBody BookingSeatClientRequestDto requestDto);

	@PutMapping("/performancehalls/seat/status/cancel")
	ApiResponseData<BookingSeatClientResponseDto> cancelSeatStatus(@RequestBody BookingSeatClientRequestDto requestDto);

	@GetMapping("/performances/{performanceId}/schedules/{performanceScheduleId}/start-time")
	ApiResponseData<PerformanceStartTimeDto> getPerformanceStartTime(@PathVariable("performanceId") UUID performanceId,
		@PathVariable("performanceScheduleId") UUID performanceScheduleId);

	@GetMapping("/performancehalls/seats/{performanceScheduleId}")
	ApiResponseData<SeatLayoutResponseDto> getSeatLayout(
		@PathVariable("performanceScheduleId") UUID performanceScheduleId);

	@GetMapping("/performancetickets/{performanceId}/schedules/{performanceScheduleId}/seats/{seatId}")
	ApiResponseData<TicketPerformanceClientResponse> getPerformanceInfo(
		@PathVariable("performanceId") UUID performanceId,
		@PathVariable("performanceScheduleId") UUID performanceScheduleId,
		@PathVariable("seatId") UUID seatId
	);
}