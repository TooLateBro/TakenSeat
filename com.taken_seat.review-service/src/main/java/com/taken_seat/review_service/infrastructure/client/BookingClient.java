package com.taken_seat.review_service.infrastructure.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.taken_seat.common_service.dto.ApiResponseData;
import com.taken_seat.review_service.infrastructure.client.dto.BookingStatusDto;

@FeignClient(name = "${feign.client.booking.name}", url = "${feign.client.booking.url}")
public interface BookingClient {

	@GetMapping("/{userId}/{performanceId}/status")
	ResponseEntity<ApiResponseData<BookingStatusDto>> getBookingStatus(@PathVariable UUID userId,
		@PathVariable UUID performanceId);
}
