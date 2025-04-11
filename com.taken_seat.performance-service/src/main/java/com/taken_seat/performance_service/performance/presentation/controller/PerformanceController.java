package com.taken_seat.performance_service.performance.presentation.controller;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.taken_seat.common_service.dto.ApiResponseData;
import com.taken_seat.common_service.dto.AuthenticatedUser;
import com.taken_seat.performance_service.performance.application.dto.request.CreateRequestDto;
import com.taken_seat.performance_service.performance.application.dto.request.SearchFilterParam;
import com.taken_seat.performance_service.performance.application.dto.request.UpdateRequestDto;
import com.taken_seat.performance_service.performance.application.dto.response.CreateResponseDto;
import com.taken_seat.performance_service.performance.application.dto.response.DetailResponseDto;
import com.taken_seat.performance_service.performance.application.dto.response.PageResponseDto;
import com.taken_seat.performance_service.performance.application.dto.response.PerformanceEndTimeDto;
import com.taken_seat.performance_service.performance.application.dto.response.UpdateResponseDto;
import com.taken_seat.performance_service.performance.application.service.PerformanceService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/performances")
public class PerformanceController {

	private final PerformanceService performanceService;

	/**
	 * 공연 생성 API
	 * 권한: ADMIN, MANAGER, PRODUCER
	 */
	@PostMapping
	public ResponseEntity<ApiResponseData<CreateResponseDto>> create(
		@Valid @RequestBody CreateRequestDto request,
		AuthenticatedUser authenticatedUser) {

		CreateResponseDto response = performanceService.create(request, authenticatedUser);
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseData.success(response));
	}

	/**
	 * 공연 전체 조회 API
	 * 권한: ALL
	 */
	@GetMapping("/search")
	public ResponseEntity<ApiResponseData<PageResponseDto>> getList(
		@ModelAttribute SearchFilterParam filterParam,
		@PageableDefault(page = 0, size = 10, sort = "startAt", direction = Sort.Direction.DESC) Pageable pageable) {

		PageResponseDto response = performanceService.search(filterParam, pageable);
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponseData.success(response));
	}

	/**
	 * 공연 상세 조회 API
	 * 권한: ALL
	 */
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponseData<DetailResponseDto>> getDetail(@PathVariable("id") UUID id) {

		DetailResponseDto response = performanceService.getDetail(id);
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponseData.success(response));
	}

	/**
	 * 공연 수정 API
	 * 권한: ADMIN, MANAGER, PRODUCER
	 */

	@PatchMapping("/{id}")
	public ResponseEntity<ApiResponseData<UpdateResponseDto>> update(@PathVariable("id") UUID id,
		@Valid @RequestBody UpdateRequestDto request,
		AuthenticatedUser authenticatedUser) {

		UpdateResponseDto response = performanceService.update(id, request, authenticatedUser);
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponseData.success(response));
	}

	/**
	 * 공연 삭제 API
	 * 권한: ADMIN, MANAGER, PRODUCER
	 */

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponseData<Void>> delete(@PathVariable("id") UUID id, @RequestParam UUID deletedBy) {

		performanceService.delete(id, deletedBy);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponseData.success());
	}

	/**
	 * 공연 종료 시간 조회 API
	 * 리뷰 등록하기 전 해당 공연이 종료되었는지 검사하기 위해 종료시간을 받아오는 메서드
	 */
	@GetMapping("/{performanceId}/schedules/{performanceScheduleId}/end-time")
	ResponseEntity<ApiResponseData<PerformanceEndTimeDto>> getPerformanceEndTime(
		@PathVariable("performanceId") UUID performanceId,
		@PathVariable("performanceScheduleId") UUID performanceScheduleId) {

		PerformanceEndTimeDto response = performanceService.getPerformanceEndTime(performanceId, performanceScheduleId);
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponseData.success(response));
	}
}
