package com.taken_seat.review_service.infrastructure.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.taken_seat.common_service.dto.ApiResponseData;
import com.taken_seat.review_service.infrastructure.client.dto.PerformanceEndTimeDto;

@FeignClient(name = "${feign.client.performance.name}", url = "${feign.client.performance.url}")
public interface PerformanceClient {

	@GetMapping("/{performanceId}/schedules/{performanceScheduleId}/end-time")
	ResponseEntity<ApiResponseData<PerformanceEndTimeDto>> getPerformanceEndTime(@PathVariable UUID performanceId,
		@PathVariable UUID performanceScheduleId);
}
