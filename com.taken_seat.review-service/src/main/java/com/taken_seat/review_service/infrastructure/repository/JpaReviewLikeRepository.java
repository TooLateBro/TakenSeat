package com.taken_seat.review_service.infrastructure.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taken_seat.review_service.domain.model.ReviewLike;
import com.taken_seat.review_service.domain.repository.ReviewLikeRepository;

public interface JpaReviewLikeRepository extends JpaRepository<ReviewLike, UUID>, ReviewLikeRepository {
}
