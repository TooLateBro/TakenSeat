package com.taken_seat.performance_service.performancehall.presentation.docs;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;

import com.taken_seat.common_service.dto.ApiResponseData;
import com.taken_seat.common_service.dto.AuthenticatedUser;
import com.taken_seat.performance_service.performancehall.presentation.dto.request.CreateRequestDto;
import com.taken_seat.performance_service.performancehall.presentation.dto.request.SearchFilterParam;
import com.taken_seat.performance_service.performancehall.presentation.dto.request.UpdateRequestDto;
import com.taken_seat.performance_service.performancehall.presentation.dto.response.CreateResponseDto;
import com.taken_seat.performance_service.performancehall.presentation.dto.response.DetailResponseDto;
import com.taken_seat.performance_service.performancehall.presentation.dto.response.PageResponseDto;
import com.taken_seat.performance_service.performancehall.presentation.dto.response.UpdateResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "공연장 API", description = "공연장 등록, 수정, 좌석 상태 변경 등의 공연장 관련 기능을 제공합니다.")
public interface PerformanceHallControllerDocs {

	@Operation(summary = "공연장 등록", description = "공연장을 등록합니다.")
	ResponseEntity<ApiResponseData<CreateResponseDto>> create(
		@Valid @RequestBody CreateRequestDto request,
		AuthenticatedUser authenticatedUser
	);

	@Operation(summary = "공연장 목록 조회", description = "검색 필터 및 페이징 정보를 기반으로 공연장 목록을 조회합니다.")
	@Parameters({
		@Parameter(name = "name", description = "공연장 이름 (부분 일치)", example = "서울 올림픽홀"),
		@Parameter(name = "address", description = "공연장 주소 (부분 일치)", example = "서울특별시 송파구 올림픽로 424"),
		@Parameter(name = "minSeats", description = "최소 좌석 수", example = "100"),
		@Parameter(name = "maxSeats", description = "최대 좌석 수", example = "500"),
		@Parameter(name = "page", description = "페이지 번호 (0부터 시작)", example = "0"),
		@Parameter(name = "size", description = "페이지 크기", example = "10"),
		@Parameter(name = "sort", description = "정렬 기준 필드", example = "name,asc")
	})
	ResponseEntity<ApiResponseData<PageResponseDto>> getList(
		@ModelAttribute SearchFilterParam filterParam,
		Pageable pageable
	);

	@Operation(summary = "공연장 상세 조회", description = "공연장 ID를 기반으로 상세 정보를 조회합니다.")
	ResponseEntity<ApiResponseData<DetailResponseDto>> getDetail(
		@Parameter(description = "공연장 ID") UUID id
	);

	@Operation(summary = "공연장 정보 수정", description = "공연장 정보를 수정합니다.")
	ResponseEntity<ApiResponseData<UpdateResponseDto>> update(
		@Parameter(description = "공연장 ID") UUID id,
		@Valid @RequestBody UpdateRequestDto request,
		AuthenticatedUser authenticatedUser
	);

	@Operation(summary = "공연장 삭제", description = "공연장을 삭제합니다.")
	ResponseEntity<ApiResponseData<Void>> delete(
		@Parameter(description = "공연장 ID") UUID id,
		AuthenticatedUser authenticatedUser
	);
}
