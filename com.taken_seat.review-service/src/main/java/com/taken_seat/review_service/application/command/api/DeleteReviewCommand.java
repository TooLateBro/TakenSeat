package com.taken_seat.review_service.application.command.api;

import com.taken_seat.review_service.application.command.ReviewCommand;
import com.taken_seat.review_service.application.dto.service.ReviewDto;
import com.taken_seat.review_service.application.service.ReviewService;

public class DeleteReviewCommand implements ReviewCommand<Void> {

	private final ReviewService reviewService;
	private final ReviewDto reviewDto;

	public DeleteReviewCommand(ReviewService reviewService, ReviewDto reviewDto) {
		this.reviewService = reviewService;
		this.reviewDto = reviewDto;
	}

	@Override
	public Void execute() {
		reviewService.deleteReview(reviewDto);
		return null;
	}
}
