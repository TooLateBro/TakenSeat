package com.taken_seat.review_service.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import com.taken_seat.review_service.domain.model.Review;

@Repository
public interface ReviewQuerydslRepository {
	Page<Review> search(String q, String category, int page, int size, String sort, String order);
}
