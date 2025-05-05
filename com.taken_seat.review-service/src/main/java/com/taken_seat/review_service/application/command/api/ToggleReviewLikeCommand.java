package com.taken_seat.review_service.application.command.api;

import com.taken_seat.review_service.application.command.ReviewCommand;
import com.taken_seat.review_service.application.dto.service.ReviewDto;
import com.taken_seat.review_service.application.service.ReviewLikeService;

public class ToggleReviewLikeCommand implements ReviewCommand<Void> {

	private final ReviewLikeService reviewLikeService;
	private final ReviewDto reviewDto;

	public ToggleReviewLikeCommand(ReviewLikeService reviewLikeService, ReviewDto reviewDto) {
		this.reviewLikeService = reviewLikeService;
		this.reviewDto = reviewDto;
	}

	@Override
	public Void execute() {
		reviewLikeService.toggleReviewLike(reviewDto);
		return null;
	}
}
