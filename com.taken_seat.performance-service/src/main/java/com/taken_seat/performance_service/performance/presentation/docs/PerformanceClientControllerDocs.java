package com.taken_seat.performance_service.performance.presentation.docs;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import com.taken_seat.common_service.dto.ApiResponseData;
import com.taken_seat.common_service.dto.request.BookingSeatClientRequestDto;
import com.taken_seat.common_service.dto.response.BookingSeatClientResponseDto;
import com.taken_seat.common_service.dto.response.PerformanceEndTimeDto;
import com.taken_seat.common_service.dto.response.PerformanceStartTimeDto;
import com.taken_seat.performance_service.performance.infrastructure.swagger.PerformanceClientSwaggerDocs;
import com.taken_seat.performance_service.performance.presentation.dto.response.SeatLayoutResponseDto;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "공연 Client API", description = "공연 회차 기반 좌석 상태 변경 및 배치도 조회 API")
public interface PerformanceClientControllerDocs {

	@PerformanceClientSwaggerDocs.UpdateSeatStatus
	ResponseEntity<ApiResponseData<BookingSeatClientResponseDto>> updateSeatStatus(
		@Valid @RequestBody BookingSeatClientRequestDto request
	);

	@PerformanceClientSwaggerDocs.CancelSeatStatus
	ResponseEntity<ApiResponseData<BookingSeatClientResponseDto>> cancelSeatStatus(
		@Valid @RequestBody BookingSeatClientRequestDto request
	);

	@PerformanceClientSwaggerDocs.GetSeatLayout
	ResponseEntity<ApiResponseData<SeatLayoutResponseDto>> getSeatLayout(
		UUID performanceScheduleId
	);

	@PerformanceClientSwaggerDocs.GetPerformanceEndTime
	ResponseEntity<ApiResponseData<PerformanceEndTimeDto>> getPerformanceEndTime(
		UUID performanceId,
		UUID performanceScheduleId
	);

	@PerformanceClientSwaggerDocs.GetPerformanceStartTime
	ResponseEntity<ApiResponseData<PerformanceStartTimeDto>> getPerformanceStartTime(
		UUID performanceId,
		UUID performanceScheduleId
	);
}

