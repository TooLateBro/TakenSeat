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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.taken_seat.performance_service.performance.application.dto.request.CreateRequestDto;
import com.taken_seat.performance_service.performance.application.dto.request.SearchFilterParam;
import com.taken_seat.performance_service.performance.application.dto.response.CreateResponseDto;
import com.taken_seat.performance_service.performance.application.dto.response.DetailResponseDto;
import com.taken_seat.performance_service.performance.application.dto.response.PageResponseDto;
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
	public ResponseEntity<CreateResponseDto> create(
		@Valid @RequestBody CreateRequestDto request) {

		CreateResponseDto response = performanceService.create(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);

	}

	/**
	 * 공연 전체 조회 API
	 * 권한: ALL
	 */
	@GetMapping("/search")
	public ResponseEntity<PageResponseDto> getList(
		@ModelAttribute SearchFilterParam filterParam,
		@PageableDefault(page = 0, size = 10, sort = "startAt", direction = Sort.Direction.DESC) Pageable pageable) {

		PageResponseDto response = performanceService.search(filterParam, pageable);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * 공연 상세 조회 API
	 * 권한: ALL
	 */
	@GetMapping("/{id}")
	public ResponseEntity<DetailResponseDto> getDetail(@PathVariable("id") UUID id) {

		DetailResponseDto response = performanceService.getDetail(id);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * 공연 삭제 API
	 * 권한: ADMIN, MANAGER, PRODUCER
	 * Security 완성 후
	 * @RequestParam UUID deletedBy -> @AuthenticationPrincipal CustomUserDetails principal 로 수정 예정
	 */

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable("id") UUID id, @RequestParam UUID deletedBy) {

		performanceService.delete(id, deletedBy);
		return ResponseEntity.noContent().build();
	}
}
