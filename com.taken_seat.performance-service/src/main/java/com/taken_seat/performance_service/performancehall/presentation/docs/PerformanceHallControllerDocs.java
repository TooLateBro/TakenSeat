package com.taken_seat.performance_service.performancehall.presentation.docs;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;

import com.taken_seat.common_service.dto.ApiResponseData;
import com.taken_seat.common_service.dto.AuthenticatedUser;
import com.taken_seat.performance_service.performancehall.infrastructure.swagger.PerformanceHallSwaggerDocs;
import com.taken_seat.performance_service.performancehall.presentation.dto.request.HallCreateRequestDto;
import com.taken_seat.performance_service.performancehall.presentation.dto.request.HallSearchFilterParam;
import com.taken_seat.performance_service.performancehall.presentation.dto.request.HallUpdateRequestDto;
import com.taken_seat.performance_service.performancehall.presentation.dto.response.HallCreateResponseDto;
import com.taken_seat.performance_service.performancehall.presentation.dto.response.HallDetailResponseDto;
import com.taken_seat.performance_service.performancehall.presentation.dto.response.HallPageResponseDto;
import com.taken_seat.performance_service.performancehall.presentation.dto.response.HallUpdateResponseDto;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "공연장 API", description = "공연장 등록, 조회, 수정, 삭제 기능을 제공합니다.")
public interface PerformanceHallControllerDocs {

	@PerformanceHallSwaggerDocs.CreateHall
	ResponseEntity<ApiResponseData<HallCreateResponseDto>> create(
		@Valid @RequestBody HallCreateRequestDto request,
		AuthenticatedUser authenticatedUser
	);

	@PerformanceHallSwaggerDocs.GetHallList
	ResponseEntity<ApiResponseData<HallPageResponseDto>> getList(
		@ModelAttribute HallSearchFilterParam filterParam,
		Pageable pageable
	);

	@PerformanceHallSwaggerDocs.GetHallDetail
	ResponseEntity<ApiResponseData<HallDetailResponseDto>> getDetail(
		UUID id
	);

	@PerformanceHallSwaggerDocs.UpdateHall
	ResponseEntity<ApiResponseData<HallUpdateResponseDto>> update(
		UUID id,
		@Valid @RequestBody HallUpdateRequestDto request,
		AuthenticatedUser authenticatedUser
	);

	@PerformanceHallSwaggerDocs.DeleteHall
	ResponseEntity<ApiResponseData<Void>> delete(
		UUID id,
		AuthenticatedUser authenticatedUser
	);
}
