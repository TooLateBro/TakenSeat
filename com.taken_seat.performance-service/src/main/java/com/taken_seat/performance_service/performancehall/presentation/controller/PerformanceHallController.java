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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taken_seat.common_service.dto.ApiResponseData;
import com.taken_seat.common_service.dto.AuthenticatedUser;
import com.taken_seat.common_service.dto.request.BookingSeatClientRequestDto;
import com.taken_seat.common_service.dto.response.BookingSeatClientResponseDto;
import com.taken_seat.performance_service.performancehall.application.dto.request.CreateRequestDto;
import com.taken_seat.performance_service.performancehall.application.dto.request.SearchFilterParam;
import com.taken_seat.performance_service.performancehall.application.dto.request.UpdateRequestDto;
import com.taken_seat.performance_service.performancehall.application.dto.response.CreateResponseDto;
import com.taken_seat.performance_service.performancehall.application.dto.response.DetailResponseDto;
import com.taken_seat.performance_service.performancehall.application.dto.response.PageResponseDto;
import com.taken_seat.performance_service.performancehall.application.dto.response.SeatLayoutResponseDto;
import com.taken_seat.performance_service.performancehall.application.dto.response.UpdateResponseDto;
import com.taken_seat.performance_service.performancehall.application.service.PerformanceHallService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/performancehalls")
public class PerformanceHallController {

	private final PerformanceHallService performanceHallService;

	@PostMapping
	public ResponseEntity<ApiResponseData<CreateResponseDto>> create(@Valid @RequestBody CreateRequestDto request,
		AuthenticatedUser authenticatedUser) {

		CreateResponseDto response = performanceHallService.create(request, authenticatedUser);
		URI location = URI.create("/api/v1/performancehalls/" + response.getPerformanceHallId());
		return ResponseEntity.created(location).body(ApiResponseData.success(response));
	}

	@GetMapping("/search")
	public ResponseEntity<ApiResponseData<PageResponseDto>> getList(
		@ModelAttribute SearchFilterParam filterParam,
		@PageableDefault(page = 0, size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {

		PageResponseDto response = performanceHallService.search(filterParam, pageable);
		return ResponseEntity.ok(ApiResponseData.success(response));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponseData<DetailResponseDto>> getDetail(@PathVariable("id") UUID id) {

		DetailResponseDto response = performanceHallService.getDetail(id);
		return ResponseEntity.ok(ApiResponseData.success(response));
	}

	@PatchMapping("/{id}")
	public ResponseEntity<ApiResponseData<UpdateResponseDto>> update(
		@PathVariable("id") UUID id,
		@Valid @RequestBody UpdateRequestDto request,
		AuthenticatedUser authenticatedUser) {

		UpdateResponseDto response = performanceHallService.update(id, request, authenticatedUser);
		return ResponseEntity.ok(ApiResponseData.success(response));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponseData<Void>> delete(@PathVariable("id") UUID id,
		AuthenticatedUser authenticatedUser) {

		performanceHallService.delete(id, authenticatedUser);
		return ResponseEntity.noContent().build();
	}

	@PutMapping("/seat/status")
	public ResponseEntity<ApiResponseData<BookingSeatClientResponseDto>> updateSeatStatus(
		@Valid @RequestBody BookingSeatClientRequestDto request) {

		BookingSeatClientResponseDto response = performanceHallService.updateSeatStatus(request);
		return ResponseEntity.ok(ApiResponseData.success(response));
	}

	@PutMapping("/seat/status/cancel")
	public ResponseEntity<ApiResponseData<BookingSeatClientResponseDto>> cancelSeatStatus(
		@Valid @RequestBody BookingSeatClientRequestDto request) {

		BookingSeatClientResponseDto response = performanceHallService.cancelSeatStatus(request);
		return ResponseEntity.ok(ApiResponseData.success(response));
	}

	@GetMapping("/seats/{performanceScheduleId}")
	public ResponseEntity<ApiResponseData<SeatLayoutResponseDto>> getSeatLayout(
		@PathVariable("performanceScheduleId") UUID performanceScheduleId) {

		SeatLayoutResponseDto response = performanceHallService.getSeatLayout(performanceScheduleId);
		return ResponseEntity.ok(ApiResponseData.success(response));
	}
}