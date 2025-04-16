package com.taken_seat.review_service.application.service;

import java.util.UUID;

import com.taken_seat.common_service.dto.AuthenticatedUser;
import com.taken_seat.review_service.application.dto.request.ReviewRegisterReqDto;
import com.taken_seat.review_service.application.dto.request.ReviewUpdateReqDto;
import com.taken_seat.review_service.application.dto.response.PageReviewResponseDto;
import com.taken_seat.review_service.application.dto.response.ReviewDetailResDto;

public interface ReviewService {

	ReviewDetailResDto registerReview(ReviewRegisterReqDto requestDto, AuthenticatedUser authenticatedUser);

	ReviewDetailResDto getReviewDetail(UUID id);

	PageReviewResponseDto searchReview(UUID performance_id, String q, String category, int page, int size,
		String sort,
		String order);

	ReviewDetailResDto updateReview(UUID id, ReviewUpdateReqDto reviewUpdateReqDto,
		AuthenticatedUser authenticatedUser);

	void deleteReview(UUID id, AuthenticatedUser authenticatedUser);
}
