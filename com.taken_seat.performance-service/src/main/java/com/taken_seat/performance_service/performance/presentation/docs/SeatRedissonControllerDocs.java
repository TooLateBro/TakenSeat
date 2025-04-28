package com.taken_seat.performance_service.performance.presentation.docs;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import com.taken_seat.common_service.dto.ApiResponseData;
import com.taken_seat.common_service.dto.request.BookingSeatClientRequestDto;
import com.taken_seat.common_service.dto.response.BookingSeatClientResponseDto;
import com.taken_seat.performance_service.performance.infrastructure.swagger.SeatRedissonSwaggerDocs;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "공연 좌석 락 API", description = "Redisson 분산 락 기반 좌석 상태 변경 API입니다.")
public interface SeatRedissonControllerDocs {

	@SeatRedissonSwaggerDocs.UpdateSeatStatusWithLock
	ResponseEntity<ApiResponseData<BookingSeatClientResponseDto>> updateSeatStatusWithLock(
		@RequestBody BookingSeatClientRequestDto request
	);

	@SeatRedissonSwaggerDocs.UpdateSeatStatusCancelWithLock
	ResponseEntity<ApiResponseData<BookingSeatClientResponseDto>> updateSeatStatusCancelWithLock(
		@RequestBody BookingSeatClientRequestDto request
	);
}

