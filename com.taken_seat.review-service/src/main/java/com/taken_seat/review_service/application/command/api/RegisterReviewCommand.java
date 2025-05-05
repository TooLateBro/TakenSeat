package com.taken_seat.review_service.application.command.api;

import com.taken_seat.review_service.application.command.ReviewCommand;
import com.taken_seat.review_service.application.dto.controller.response.ReviewDetailResDto;
import com.taken_seat.review_service.application.dto.service.ReviewDto;
import com.taken_seat.review_service.application.service.ReviewService;

public class RegisterReviewCommand implements ReviewCommand<ReviewDetailResDto> {

	private final ReviewService reviewService;
	private final ReviewDto reviewDto;

	public RegisterReviewCommand(ReviewService reviewService, ReviewDto reviewDto) {
		this.reviewService = reviewService;
		this.reviewDto = reviewDto;
	}

	@Override
	public ReviewDetailResDto execute() {
		return reviewService.registerReview(reviewDto);
	}
}
