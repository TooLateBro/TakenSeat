package com.taken_seat.review_service.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.taken_seat.review_service.domain.model.Review;

import jakarta.persistence.LockModeType;

@Repository
public interface ReviewRepository {

	Review save(Review review);

	Boolean existsByAuthorIdAndPerformanceId(UUID authorId, UUID performanceId);

	Optional<Review> findByIdAndDeletedAtIsNull(UUID id);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT r FROM Review r WHERE r.id = :id AND r.deletedAt IS NULL ")
	Optional<Review> findWithPessimisticLockById(UUID id);

}
