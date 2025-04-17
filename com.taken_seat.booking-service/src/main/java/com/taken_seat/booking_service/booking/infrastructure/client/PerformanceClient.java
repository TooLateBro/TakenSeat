package com.taken_seat.booking_service.booking.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.taken_seat.common_service.dto.ApiResponseData;
import com.taken_seat.common_service.dto.request.BookingSeatClientRequestDto;
import com.taken_seat.common_service.dto.response.BookingSeatClientResponseDto;

@FeignClient(name = "performance-service", path = "/api/v1/performancehalls", contextId = "booking")
public interface PerformanceClient {

	@PutMapping("/seat/status")
	ApiResponseData<BookingSeatClientResponseDto> updateSeatStatus(@RequestBody BookingSeatClientRequestDto requestDto);

	@PutMapping("/seat/status/cancel")
	ApiResponseData<BookingSeatClientResponseDto> cancelSeatStatus(@RequestBody BookingSeatClientRequestDto requestDto);
}