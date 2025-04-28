package com.taken_seat.performance_service.performancehall.presentation.controller;

import java.net.URI;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
import com.taken_seat.performance_service.performancehall.application.service.PerformanceHallService;
import com.taken_seat.performance_service.performancehall.presentation.docs.PerformanceHallControllerDocs;
import com.taken_seat.performance_service.performancehall.presentation.dto.request.HallCreateRequestDto;
import com.taken_seat.performance_service.performancehall.presentation.dto.request.HallSearchFilterParam;
import com.taken_seat.performance_service.performancehall.presentation.dto.request.HallUpdateRequestDto;
import com.taken_seat.performance_service.performancehall.presentation.dto.response.HallCreateResponseDto;
import com.taken_seat.performance_service.performancehall.presentation.dto.response.HallDetailResponseDto;
import com.taken_seat.performance_service.performancehall.presentation.dto.response.HallPageResponseDto;
import com.taken_seat.performance_service.performancehall.presentation.dto.response.HallUpdateResponseDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/performancehalls")
public class PerformanceHallController implements PerformanceHallControllerDocs {

	private final PerformanceHallService performanceHallService;

	@RoleCheck(allowedRoles = {Role.ADMIN, Role.MANAGER})
	@PostMapping
	public ResponseEntity<ApiResponseData<HallCreateResponseDto>> create(
		@Valid @RequestBody HallCreateRequestDto request,
		AuthenticatedUser authenticatedUser) {

		HallCreateResponseDto response = performanceHallService.create(request, authenticatedUser);
		URI location = URI.create("/api/v1/performancehalls/" + response.performanceHallId());
		return ResponseEntity.created(location).body(ApiResponseData.success(response));
	}

	@GetMapping("/search")
	public ResponseEntity<ApiResponseData<HallPageResponseDto>> getList(
		@ModelAttribute HallSearchFilterParam filterParam,
		@PageableDefault(page = 0, size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {

		HallPageResponseDto response = performanceHallService.search(filterParam, pageable);
		return ResponseEntity.ok(ApiResponseData.success(response));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponseData<HallDetailResponseDto>> getDetail(@PathVariable("id") UUID id) {

		HallDetailResponseDto response = performanceHallService.getDetail(id);
		return ResponseEntity.ok(ApiResponseData.success(response));
	}

	@RoleCheck(allowedRoles = {Role.ADMIN, Role.MANAGER})
	@PatchMapping("/{id}")
	public ResponseEntity<ApiResponseData<HallUpdateResponseDto>> update(
		@PathVariable("id") UUID id,
		@Valid @RequestBody HallUpdateRequestDto request,
		AuthenticatedUser authenticatedUser) {

		HallUpdateResponseDto response = performanceHallService.update(id, request, authenticatedUser);
		return ResponseEntity.ok(ApiResponseData.success(response));
	}

	@RoleCheck(allowedRoles = {Role.ADMIN, Role.MANAGER})
	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponseData<Void>> delete(@PathVariable("id") UUID id,
		AuthenticatedUser authenticatedUser) {

		performanceHallService.delete(id, authenticatedUser);
		return ResponseEntity.noContent().build();
	}
}