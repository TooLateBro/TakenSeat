package com.taken_seat.review_service.domain.repository;

import org.springframework.data.domain.Page;

import com.taken_seat.review_service.domain.model.Review;

public interface ReviewQuerydslRepository {
	Page<Review> search(String q, String category, int page, int size, String sort, String order);
}
