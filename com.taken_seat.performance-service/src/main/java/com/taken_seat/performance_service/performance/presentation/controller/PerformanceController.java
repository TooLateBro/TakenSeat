package com.taken_seat.performance_service.performance.presentation.controller;

import java.net.URI;
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
import org.springframework.web.bind.annotation.RestController;

import com.taken_seat.common_service.aop.annotation.RoleCheck;
import com.taken_seat.common_service.aop.vo.Role;
import com.taken_seat.common_service.dto.ApiResponseData;
import com.taken_seat.common_service.dto.AuthenticatedUser;
import com.taken_seat.performance_service.performance.application.service.PerformanceService;
import com.taken_seat.performance_service.performance.presentation.docs.PerformanceControllerDocs;
import com.taken_seat.performance_service.performance.presentation.dto.request.CreateRequestDto;
import com.taken_seat.performance_service.performance.presentation.dto.request.SearchFilterParam;
import com.taken_seat.performance_service.performance.presentation.dto.request.UpdateRequestDto;
import com.taken_seat.performance_service.performance.presentation.dto.response.CreateResponseDto;
import com.taken_seat.performance_service.performance.presentation.dto.response.DetailResponseDto;
import com.taken_seat.performance_service.performance.presentation.dto.response.PageResponseDto;
import com.taken_seat.performance_service.performance.presentation.dto.response.UpdateResponseDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/performances")
public class PerformanceController implements PerformanceControllerDocs {

	private final PerformanceService performanceService;

	@RoleCheck(allowedRoles = {Role.ADMIN, Role.MANAGER, Role.PRODUCER})
	@PostMapping
	public ResponseEntity<ApiResponseData<CreateResponseDto>> create(
		@Valid @RequestBody CreateRequestDto request,
		AuthenticatedUser authenticatedUser) {

		CreateResponseDto response = performanceService.create(request, authenticatedUser);
		URI location = URI.create("/api/v1/performances/" + response.performanceId());
		return ResponseEntity.created(location).body(ApiResponseData.success(response));
	}

	@GetMapping("/search")
	public ResponseEntity<ApiResponseData<PageResponseDto>> getList(
		@ModelAttribute SearchFilterParam filterParam,
		@PageableDefault(page = 0, size = 10, sort = "startAt", direction = Sort.Direction.DESC) Pageable pageable) {

		PageResponseDto response = performanceService.search(filterParam, pageable);
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponseData.success(response));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponseData<DetailResponseDto>> getDetail(@PathVariable("id") UUID id) {

		DetailResponseDto response = performanceService.getDetail(id);
		return ResponseEntity.ok(ApiResponseData.success(response));
	}

	@RoleCheck(allowedRoles = {Role.ADMIN, Role.MANAGER, Role.PRODUCER})
	@PatchMapping("/{id}")
	public ResponseEntity<ApiResponseData<UpdateResponseDto>> update(@PathVariable("id") UUID id,
		@Valid @RequestBody UpdateRequestDto request,
		AuthenticatedUser authenticatedUser) {

		UpdateResponseDto response = performanceService.update(id, request, authenticatedUser);
		return ResponseEntity.ok(ApiResponseData.success(response));
	}

	@RoleCheck(allowedRoles = {Role.ADMIN, Role.MANAGER, Role.PRODUCER})
	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponseData<Void>> delete(@PathVariable("id") UUID id,
		AuthenticatedUser authenticatedUser) {

		performanceService.delete(id, authenticatedUser);
		return ResponseEntity.noContent().build();
	}

	@RoleCheck(allowedRoles = {Role.ADMIN, Role.MANAGER, Role.PRODUCER})
	@PatchMapping("/{id}/status")
	public ResponseEntity<ApiResponseData<Void>> updateStatus(
		@PathVariable("id") UUID id,
		AuthenticatedUser authenticatedUser) {

		performanceService.updateStatus(id, authenticatedUser);
		return ResponseEntity.noContent().build();
	}

	@RoleCheck(allowedRoles = {Role.ADMIN, Role.MANAGER, Role.PRODUCER})
	@DeleteMapping("/{performanceId}/schedules/{performanceScheduleId}")
	public ResponseEntity<ApiResponseData<Void>> deletePerformanceSchedule(
		@PathVariable("performanceId") UUID performanceId,
		@PathVariable("performanceScheduleId") UUID performanceScheduleId,
		AuthenticatedUser authenticatedUser) {

		performanceService.deletePerformanceSchedule(performanceId, performanceScheduleId, authenticatedUser);
		return ResponseEntity.noContent().build();
	}
}
