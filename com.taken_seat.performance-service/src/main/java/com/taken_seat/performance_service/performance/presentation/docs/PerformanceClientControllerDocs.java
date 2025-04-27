package com.taken_seat.performance_service.performance.presentation.docs;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import com.taken_seat.common_service.dto.ApiResponseData;
import com.taken_seat.common_service.dto.request.BookingSeatClientRequestDto;
import com.taken_seat.common_service.dto.response.BookingSeatClientResponseDto;
import com.taken_seat.common_service.dto.response.PerformanceEndTimeDto;
import com.taken_seat.common_service.dto.response.PerformanceStartTimeDto;
import com.taken_seat.performance_service.performance.presentation.dto.response.SeatLayoutResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "공연 Client API", description = "공연 회차 기반 좌석 상태 변경 및 배치도 조회 API")
public interface PerformanceClientControllerDocs {

	@Operation(summary = "좌석 상태 변경", description = "좌석 예약 시 좌석 상태를 변경합니다.")
	ResponseEntity<ApiResponseData<BookingSeatClientResponseDto>> updateSeatStatus(
		@Valid @RequestBody BookingSeatClientRequestDto request
	);

	@Operation(summary = "좌석 상태 취소", description = "예약 취소 시 좌석 상태를 원래대로 되돌립니다.")
	ResponseEntity<ApiResponseData<BookingSeatClientResponseDto>> cancelSeatStatus(
		@Valid @RequestBody BookingSeatClientRequestDto request
	);

	@Operation(summary = "좌석 배치도 조회", description = "공연 스케줄 ID를 기반으로 공연 회차 좌석 배치도를 조회합니다.")
	ResponseEntity<ApiResponseData<SeatLayoutResponseDto>> getSeatLayout(UUID performanceScheduleId);

	@Operation(summary = "공연 종료 시간 조회", description = "리뷰 작성 가능 여부 판단에 사용됩니다.")
	ResponseEntity<ApiResponseData<PerformanceEndTimeDto>> getPerformanceEndTime(UUID performanceId,
		UUID performanceScheduleId);

	@Operation(summary = "공연 시작 시간 조회", description = "환불 가능 여부 판단에 사용됩니다.")
	ResponseEntity<ApiResponseData<PerformanceStartTimeDto>> getPerformanceStartTime(UUID performanceId,
		UUID performanceScheduleId);
}

