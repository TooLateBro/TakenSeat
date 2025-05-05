package com.taken_seat.review_service.application.command.api;

import java.util.UUID;

import com.taken_seat.review_service.application.command.ReviewCommand;
import com.taken_seat.review_service.application.dto.controller.response.ReviewDetailResDto;
import com.taken_seat.review_service.application.service.ReviewService;

public class GetReviewDetailCommand implements ReviewCommand<ReviewDetailResDto> {

	private final ReviewService reviewService;
	private final UUID reviewId;

	public GetReviewDetailCommand(ReviewService reviewService, UUID reviewId) {
		this.reviewService = reviewService;
		this.reviewId = reviewId;
	}

	@Override
	public ReviewDetailResDto execute() {
		return reviewService.getReviewDetail(reviewId);
	}
}
