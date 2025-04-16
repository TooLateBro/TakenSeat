package com.taken_seat.review_service.application.scheduler;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.taken_seat.review_service.domain.repository.RedisRatingRepository;
import com.taken_seat.review_service.domain.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AvgRatingBulkScheduler {

	private final RedisRatingRepository redisRatingRepository;
	private final ReviewRepository reviewRepository;

	// 개발 도중에는테스트를 위해 1분마다 실행되게 했습니다.
	@Scheduled(cron = "0 */1 * * * ?")
	@Caching(evict = {
		@CacheEvict(cacheNames = "reviewCache", allEntries = true),
		@CacheEvict(cacheNames = "reviewSearchCache", allEntries = true)
	})
	public void fetchPerformanceRatingStatsBulk() {
		redisRatingRepository.setAvgRatingBulk();
	}

}
