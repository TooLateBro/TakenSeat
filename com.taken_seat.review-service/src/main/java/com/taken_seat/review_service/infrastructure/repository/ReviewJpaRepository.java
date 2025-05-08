package com.taken_seat.review_service.infrastructure.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.taken_seat.review_service.domain.model.Review;

public interface ReviewJpaRepository extends JpaRepository<Review, UUID> {

	Boolean existsByAuthorIdAndPerformanceIdAndDeletedAtIsNull(UUID authorId, UUID performanceId);

	Optional<Review> findByIdAndDeletedAtIsNull(UUID id);

	<S extends Review> List<S> saveAllAndFlush(Iterable<S> entities);

	@Query(value =
		"SELECT r.performance_id AS performanceId, AVG(r.rating) AS avgRating, COUNT(r.id) AS reviewCount "
			+ "FROM p_review r "
			+ "WHERE r.performance_id = :performanceId AND r.deleted_at IS NULL", nativeQuery = true)
	Map<String, Object> fetchAvgRatingAndReviewCountByPerformanceId(UUID performanceId);

	@Query(value = """
		    SELECT
		        r.performance_id,
		        ROUND(AVG(r.rating), 2) AS avgRating,
		        COUNT(*) AS reviewCount
		    FROM p_review r
		    WHERE r.performance_id IN :performanceIds
		    GROUP BY r.performance_id
		""", nativeQuery = true)
	List<Map<String, Object>> fetchAvgRatingAndReviewCountByPerformanceIds(
		@Param("performanceIds") List<UUID> performanceIds);

}
