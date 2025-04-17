package com.taken_seat.review_service.domain.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;

import com.taken_seat.review_service.domain.model.Review;

public interface ReviewQuerydslRepository {
	Page<Review> search(UUID performance_id, String q, String category, int page, int size, String sort,
		String order);
}
