package com.taken_seat.review_service.domain.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.taken_seat.review_service.domain.model.Review;

@Repository
public interface ReviewRepository {

	Review save(Review review);

	Boolean existsByAuthorIdAndPerformanceId(UUID authorId, UUID performanceId);

	Optional<Review> findByIdAndDeletedAtIsNull(UUID id);

	<S extends Review> List<S> saveAllAndFlush(Iterable<S> entities);

	@Query(value =
		"SELECT r.performance_id AS performanceId, AVG(r.rating) AS avgRating, COUNT(r.id) AS reviewCount " +
			"FROM p_review r " +
			"WHERE r.deleted_at IS NULL " +
			"GROUP BY r.performance_id",
		nativeQuery = true)
	List<Map<String, Object>> fetchPerformanceRatingStatsBulk();
}
