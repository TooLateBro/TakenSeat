package com.taken_seat.review_service.domain.repository;

import org.springframework.data.domain.Page;

import com.taken_seat.review_service.application.dto.service.ReviewSearchDto;
import com.taken_seat.review_service.domain.model.Review;

public interface CustomReviewQuerydslRepository {
	Page<Review> search(ReviewSearchDto dto);
}
