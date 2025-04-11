package com.taken_seat.review_service.infrastructure.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taken_seat.review_service.domain.model.Review;
import com.taken_seat.review_service.domain.repository.ReviewRepository;

public interface JpaReviewRepository extends JpaRepository<Review, UUID>, ReviewRepository {
}
