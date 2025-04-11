package com.taken_seat.review_service.presentation.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.taken_seat.common_service.dto.ApiResponseData;
import com.taken_seat.common_service.dto.AuthenticatedUser;
import com.taken_seat.review_service.application.dto.request.ReviewRegisterReqDto;
import com.taken_seat.review_service.application.dto.response.PageReviewResponseDto;
import com.taken_seat.review_service.application.dto.response.ReviewDetailResDto;
import com.taken_seat.review_service.application.service.ReviewService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reviews")
public class ReviewController {

	private final ReviewService reviewService;

	@PostMapping
	public ResponseEntity<ApiResponseData<ReviewDetailResDto>> registerReview(
		@Valid @RequestBody ReviewRegisterReqDto reviewRegisterReqDto,
		AuthenticatedUser authenticatedUser) {

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponseData.success(reviewService.registerReview(reviewRegisterReqDto, authenticatedUser)));

	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponseData<ReviewDetailResDto>> getReviewDetail(@PathVariable("id") UUID id) {
		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponseData.success(reviewService.getReviewDetail(id)));
	}

	@GetMapping("/search")
	public ResponseEntity<ApiResponseData<PageReviewResponseDto>> searchPayment(
		@RequestParam(required = false) String q,
		@RequestParam(required = false) String category,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(defaultValue = "createdAt") String sort,
		@RequestParam(defaultValue = "desc") String order) {
		return ResponseEntity.status(HttpStatus.OK)
			.body(
				ApiResponseData.success(reviewService.searchPayment(q, category, page, size, sort, order)));
	}

}
