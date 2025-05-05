package com.taken_seat.review_service.presentation.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.taken_seat.common_service.aop.annotation.RoleCheck;
import com.taken_seat.common_service.aop.vo.Role;
import com.taken_seat.common_service.dto.ApiResponseData;
import com.taken_seat.common_service.dto.AuthenticatedUser;
import com.taken_seat.review_service.application.command.api.GetReviewDetailCommand;
import com.taken_seat.review_service.application.command.api.RegisterReviewCommand;
import com.taken_seat.review_service.application.command.api.SearchReviewCommand;
import com.taken_seat.review_service.application.command.api.UpdateReviewCommand;
import com.taken_seat.review_service.application.dto.controller.request.ReviewRegisterReqDto;
import com.taken_seat.review_service.application.dto.controller.request.ReviewUpdateReqDto;
import com.taken_seat.review_service.application.dto.controller.response.PageReviewResponseDto;
import com.taken_seat.review_service.application.dto.controller.response.ReviewDetailResDto;
import com.taken_seat.review_service.application.dto.service.ReviewDto;
import com.taken_seat.review_service.application.dto.service.ReviewSearchDto;
import com.taken_seat.review_service.application.service.ReviewLikeService;
import com.taken_seat.review_service.application.service.ReviewService;
import com.taken_seat.review_service.infrastructure.mapper.ReviewMapper;
import com.taken_seat.review_service.infrastructure.swagger.ReviewSwaggerDocs;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reviews")
@Tag(name = "Review API", description = "리뷰 관련 API")
public class ReviewController {

	private final ReviewService reviewServices;
	private final ReviewLikeService reviewLikeService;
	private final ReviewMapper reviewMapper;

	@PostMapping
	@RoleCheck(allowedRoles = {Role.ADMIN, Role.MANAGER, Role.CUSTOMER})
	@ReviewSwaggerDocs.RegisterReview
	public ResponseEntity<ApiResponseData<ReviewDetailResDto>> registerReview(
		@Valid @RequestBody ReviewRegisterReqDto reviewRegisterReqDto,
		AuthenticatedUser authenticatedUser) {

		ReviewDto dto = reviewMapper.toDto(reviewRegisterReqDto, authenticatedUser);

		RegisterReviewCommand command = new RegisterReviewCommand(reviewServices, dto);

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponseData.success(command.execute()));

	}

	@GetMapping("/{reviewId}")
	@ReviewSwaggerDocs.GetPaymentDetail
	public ResponseEntity<ApiResponseData<ReviewDetailResDto>> getReviewDetail(
		@PathVariable("reviewId") UUID reviewId) {

		GetReviewDetailCommand command = new GetReviewDetailCommand(reviewServices, reviewId);

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponseData.success(command.execute()));
	}

	@GetMapping("/search")
	@ReviewSwaggerDocs.SearchReview
	public ResponseEntity<ApiResponseData<PageReviewResponseDto>> searchReview(
		@RequestParam(required = true) UUID performance_id,
		@RequestParam(required = false) String q,
		@RequestParam(required = false) String category,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(defaultValue = "createdAt") String sort,
		@RequestParam(defaultValue = "desc") String order) {

		ReviewSearchDto dto = reviewMapper.toDto(performance_id, q, category, page, size, sort, order);

		SearchReviewCommand command = new SearchReviewCommand(reviewServices, dto);

		return ResponseEntity.status(HttpStatus.OK)
			.body(
				ApiResponseData.success(command.execute()));
	}

	@PatchMapping("/{reviewId}")
	@RoleCheck(allowedRoles = {Role.ADMIN, Role.MANAGER, Role.CUSTOMER})
	@ReviewSwaggerDocs.UpdateReview
	public ResponseEntity<ApiResponseData<ReviewDetailResDto>> updateReview(@PathVariable("reviewId") UUID reviewId,
		@Valid @RequestBody ReviewUpdateReqDto reviewUpdateReqDto,
		AuthenticatedUser authenticatedUser) {

		ReviewDto dto = reviewMapper.toDto(reviewId, reviewUpdateReqDto, authenticatedUser);

		UpdateReviewCommand command = new UpdateReviewCommand(reviewServices, dto);

		return ResponseEntity.status(HttpStatus.OK)
			.body(
				ApiResponseData.success(command.execute()));
	}

	@DeleteMapping("/{reviewId}")
	@RoleCheck(allowedRoles = {Role.ADMIN, Role.MANAGER, Role.CUSTOMER})
	@ReviewSwaggerDocs.DeleteReview
	public ResponseEntity<ApiResponseData<Void>> deleteReview(@PathVariable("reviewId") UUID reviewId,
		AuthenticatedUser authenticatedUser) {

		ReviewDto dto = reviewMapper.toDto(reviewId, authenticatedUser);

		reviewServices.deleteReview(dto);
		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponseData.success());
	}

	@PostMapping("/{reviewId}/like")
	@ReviewSwaggerDocs.ToggleReviewLike
	public ResponseEntity<ApiResponseData<Void>> toggleReviewLike(@PathVariable("reviewId") UUID reviewId,
		AuthenticatedUser authenticatedUser) {
		reviewLikeService.toggleReviewLike(reviewId, authenticatedUser);

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponseData.success());
	}

}

