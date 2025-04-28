package com.taken_seat.review_service.infrastructure.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.taken_seat.review_service.domain.model.Review;
import com.taken_seat.review_service.domain.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements ReviewRepository {

	private final ReviewJpaRepository reviewJpaRepository;

	@Override
	public Review save(Review review) {
		return reviewJpaRepository.save(review);
	}

	@Override
	public Boolean existsByAuthorIdAndPerformanceIdAndDeletedAtIsNull(UUID authorId, UUID performanceId) {
		return reviewJpaRepository.existsByAuthorIdAndPerformanceIdAndDeletedAtIsNull(authorId, performanceId);
	}

	@Override
	public Optional<Review> findByIdAndDeletedAtIsNull(UUID id) {
		return reviewJpaRepository.findByIdAndDeletedAtIsNull(id);
	}

	@Override
	public <S extends Review> List<S> saveAllAndFlush(Iterable<S> entities) {
		return reviewJpaRepository.saveAllAndFlush(entities);
	}

	@Override
	public Map<String, Object> fetchAvgRatingAndReviewCountByPerformanceId(UUID performanceId) {
		return reviewJpaRepository.fetchAvgRatingAndReviewCountByPerformanceId(performanceId);
	}

	@Override
	public List<Map<String, Object>> fetchPerformanceRatingStatsBulk() {
		return reviewJpaRepository.fetchPerformanceRatingStatsBulk();
	}
}
