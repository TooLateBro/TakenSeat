package com.taken_seat.review_service.domain.repository;

import java.util.UUID;

import org.springframework.stereotype.Repository;

@Repository
public interface RedisRatingRepository {

	Double getAvgRating(UUID performanceId);

	void setAvgRatingForChangedPerformances();
}
