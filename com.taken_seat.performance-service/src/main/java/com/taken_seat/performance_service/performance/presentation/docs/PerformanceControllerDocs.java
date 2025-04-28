package com.taken_seat.performance_service.performance.presentation.docs;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;

import com.taken_seat.common_service.dto.ApiResponseData;
import com.taken_seat.common_service.dto.AuthenticatedUser;
import com.taken_seat.performance_service.performance.infrastructure.swagger.PerformanceSwaggerDocs;
import com.taken_seat.performance_service.performance.presentation.dto.request.CreateRequestDto;
import com.taken_seat.performance_service.performance.presentation.dto.request.SearchFilterParam;
import com.taken_seat.performance_service.performance.presentation.dto.request.UpdateRequestDto;
import com.taken_seat.performance_service.performance.presentation.dto.response.CreateResponseDto;
import com.taken_seat.performance_service.performance.presentation.dto.response.DetailResponseDto;
import com.taken_seat.performance_service.performance.presentation.dto.response.PageResponseDto;
import com.taken_seat.performance_service.performance.presentation.dto.response.UpdateResponseDto;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "공연 API", description = "공연 등록, 조회, 수정, 삭제 기능을 제공합니다.")
public interface PerformanceControllerDocs {

	@PerformanceSwaggerDocs.CreatePerformance
	ResponseEntity<ApiResponseData<CreateResponseDto>> create(
		@Valid @RequestBody CreateRequestDto request,
		AuthenticatedUser authenticatedUser
	);

	@PerformanceSwaggerDocs.GetPerformanceList
	ResponseEntity<ApiResponseData<PageResponseDto>> getList(
		@ModelAttribute SearchFilterParam filterParam,
		Pageable pageable
	);

	@PerformanceSwaggerDocs.GetPerformanceDetail
	ResponseEntity<ApiResponseData<DetailResponseDto>> getDetail(
		UUID id
	);

	@PerformanceSwaggerDocs.UpdatePerformance
	ResponseEntity<ApiResponseData<UpdateResponseDto>> update(
		UUID id,
		@Valid @RequestBody UpdateRequestDto request,
		AuthenticatedUser authenticatedUser
	);

	@PerformanceSwaggerDocs.DeletePerformance
	ResponseEntity<ApiResponseData<Void>> delete(
		UUID id,
		AuthenticatedUser authenticatedUser
	);

	@PerformanceSwaggerDocs.UpdatePerformanceStatus
	ResponseEntity<ApiResponseData<Void>> updateStatus(
		UUID id,
		AuthenticatedUser authenticatedUser
	);

	@PerformanceSwaggerDocs.DeletePerformanceSchedule
	ResponseEntity<ApiResponseData<Void>> deletePerformanceSchedule(
		UUID performanceId,
		UUID performanceScheduleId,
		AuthenticatedUser authenticatedUser
	);
}
