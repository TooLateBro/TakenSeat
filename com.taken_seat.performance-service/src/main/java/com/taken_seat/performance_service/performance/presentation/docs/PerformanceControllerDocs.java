package com.taken_seat.performance_service.performance.presentation.docs;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;

import com.taken_seat.common_service.dto.ApiResponseData;
import com.taken_seat.common_service.dto.AuthenticatedUser;
import com.taken_seat.performance_service.performance.presentation.dto.request.CreateRequestDto;
import com.taken_seat.performance_service.performance.presentation.dto.request.SearchFilterParam;
import com.taken_seat.performance_service.performance.presentation.dto.request.UpdateRequestDto;
import com.taken_seat.performance_service.performance.presentation.dto.response.CreateResponseDto;
import com.taken_seat.performance_service.performance.presentation.dto.response.DetailResponseDto;
import com.taken_seat.performance_service.performance.presentation.dto.response.PageResponseDto;
import com.taken_seat.performance_service.performance.presentation.dto.response.UpdateResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "공연 API", description = "공연 등록 및 조회, 수정, 삭제 등의 공연 관련 기능을 제공합니다.")
public interface PerformanceControllerDocs {

	@Operation(summary = "공연 등록", description = "새로운 공연 정보를 등록합니다.")
	ResponseEntity<ApiResponseData<CreateResponseDto>> create(
		@Valid @RequestBody CreateRequestDto request,
		AuthenticatedUser authenticatedUser
	);

	@Operation(summary = "공연 목록 조회", description = "검색 필터와 페이징 정보를 기반으로 공연 목록을 조회합니다.")
	ResponseEntity<ApiResponseData<PageResponseDto>> getList(
		@ModelAttribute SearchFilterParam filterParam,
		Pageable pageable
	);

	@Operation(summary = "공연 상세 조회", description = "공연 ID를 기반으로 공연의 상세 정보를 조회합니다.")
	ResponseEntity<ApiResponseData<DetailResponseDto>> getDetail(UUID id);

	@Operation(summary = "공연 수정", description = "공연 ID를 기반으로 공연 정보를 수정합니다.")
	ResponseEntity<ApiResponseData<UpdateResponseDto>> update(UUID id, @RequestBody UpdateRequestDto request,
		AuthenticatedUser authenticatedUser);

	@Operation(summary = "공연 삭제", description = "공연 ID를 기반으로 공연 정보를 삭제합니다.")
	ResponseEntity<ApiResponseData<Void>> delete(UUID id, AuthenticatedUser authenticatedUser);

	@Operation(summary = "공연 상태 변경", description = "공연 ID를 기반으로 공연 상태를 변경합니다.")
	ResponseEntity<ApiResponseData<Void>> updateStatus(UUID id, AuthenticatedUser authenticatedUser);
}