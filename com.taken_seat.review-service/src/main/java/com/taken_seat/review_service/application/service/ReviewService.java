package com.taken_seat.review_service.application.service;

import java.util.UUID;

import com.taken_seat.review_service.application.dto.controller.response.PageReviewResponseDto;
import com.taken_seat.review_service.application.dto.controller.response.ReviewDetailResDto;
import com.taken_seat.review_service.application.dto.service.ReviewDto;
import com.taken_seat.review_service.application.dto.service.ReviewSearchDto;

public interface ReviewService {

	ReviewDetailResDto registerReview(ReviewDto dto);

	ReviewDetailResDto getReviewDetail(UUID id);

	PageReviewResponseDto searchReview(ReviewSearchDto dto);

	ReviewDetailResDto updateReview(ReviewDto dto);

	void deleteReview(ReviewDto dto);
}
