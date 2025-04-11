package com.taken_seat.performance_service.performancehall.presentation.controller;

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

import com.taken_seat.performance_service.performancehall.application.dto.request.CreateRequestDto;
import com.taken_seat.performance_service.performancehall.application.dto.request.SearchFilterParam;
import com.taken_seat.performance_service.performancehall.application.dto.request.UpdateRequestDto;
import com.taken_seat.performance_service.performancehall.application.dto.response.CreateResponseDto;
import com.taken_seat.performance_service.performancehall.application.dto.response.DetailResponseDto;
import com.taken_seat.performance_service.performancehall.application.dto.response.PageResponseDto;
import com.taken_seat.performance_service.performancehall.application.dto.response.UpdateResponseDto;
import com.taken_seat.performance_service.performancehall.application.service.PerformanceHallService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/performancehalls")
public class PerformanceHallController {

	private final PerformanceHallService performanceHallService;

	/**
	 * 공연장 생성 API
	 * 권한: ADMIN, MANAGER
	 */
	@PostMapping
	public ResponseEntity<CreateResponseDto> create(@Valid @RequestBody CreateRequestDto request) {

		CreateResponseDto response = performanceHallService.create(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	/**
	 * 공연장 전체 조회 API
	 * 권한: ALL
	 */
	@GetMapping("/search")
	public ResponseEntity<PageResponseDto> getList(
		@ModelAttribute SearchFilterParam filterParam,
		@PageableDefault(page = 0, size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {

		PageResponseDto response = performanceHallService.search(filterParam, pageable);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * 공연장 상세 조회 API
	 * 권한: ALL
	 */
	@GetMapping("/{id}")
	public ResponseEntity<DetailResponseDto> getDetail(@PathVariable("id") UUID id) {

		DetailResponseDto response = performanceHallService.getDetail(id);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * 공연장 수정 API
	 * 권한: ADMIN, MANAGER
	 */
	@PatchMapping("/{id}")
	public ResponseEntity<UpdateResponseDto> update(
		@PathVariable("id") UUID id,
		@Valid @RequestBody UpdateRequestDto request) {

		UpdateResponseDto response = performanceHallService.update(id, request);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * 공연장 삭제 API
	 * 권한: ADMIN, MANAGER
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable("id") UUID id, @RequestParam UUID deletedBy) {

		performanceHallService.delete(id, deletedBy);
		return ResponseEntity.noContent().build();
	}
}