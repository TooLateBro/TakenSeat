package com.taken_seat.review_service.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.taken_seat.review_service.domain.model.Review;

@Repository
public interface ReviewRepository {
	
	Review save(Review review);

	Boolean existsByAuthorIdAndPerformanceId(UUID authorId, UUID performanceId);

	Optional<Review> findByIdAndDeletedAtIsNull(UUID id);

}
