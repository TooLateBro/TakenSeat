package com.taken_seat.review_service.application.command.api;

import com.taken_seat.review_service.application.command.ReviewCommand;
import com.taken_seat.review_service.application.dto.controller.response.PageReviewResponseDto;
import com.taken_seat.review_service.application.dto.service.ReviewSearchDto;
import com.taken_seat.review_service.application.service.ReviewService;

public class SearchReviewCommand implements ReviewCommand<PageReviewResponseDto> {

	private final ReviewService reviewService;
	private final ReviewSearchDto reviewSearchDto;

	public SearchReviewCommand(ReviewService reviewService, ReviewSearchDto reviewSearchDto) {
		this.reviewService = reviewService;
		this.reviewSearchDto = reviewSearchDto;
	}

	@Override
	public PageReviewResponseDto execute() {
		return reviewService.searchReview(reviewSearchDto);
	}
}
