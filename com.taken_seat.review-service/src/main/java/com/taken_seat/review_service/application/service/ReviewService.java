package com.taken_seat.review_service.application.service;

import java.util.UUID;

import com.taken_seat.review_service.application.dto.controller.response.PageReviewResponseDto;
import com.taken_seat.review_service.application.dto.controller.response.ReviewDetailResDto;
import com.taken_seat.review_service.application.dto.service.ReviewDto;

public interface ReviewService {

	ReviewDetailResDto registerReview(ReviewDto dto);

	ReviewDetailResDto getReviewDetail(UUID id);

	PageReviewResponseDto searchReview(UUID performance_id, String q, String category, int page, int size,
		String sort,
		String order);

	ReviewDetailResDto updateReview(ReviewDto dto);

	void deleteReview(ReviewDto dto);
}
