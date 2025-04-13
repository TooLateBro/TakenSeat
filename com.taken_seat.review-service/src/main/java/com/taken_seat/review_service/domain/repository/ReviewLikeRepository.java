package com.taken_seat.review_service.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.taken_seat.review_service.domain.model.ReviewLike;

@Repository
public interface ReviewLikeRepository {

	Optional<ReviewLike> findByAuthorIdAndReviewId(UUID authorId, UUID reviewId);

	ReviewLike save(ReviewLike reviewLike);
}
