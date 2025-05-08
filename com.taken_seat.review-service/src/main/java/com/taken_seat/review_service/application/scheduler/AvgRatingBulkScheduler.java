package com.taken_seat.review_service.application.scheduler;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.taken_seat.review_service.domain.repository.RedisRatingRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AvgRatingBulkScheduler {

	private final RedisRatingRepository redisRatingRepository;

	// 한시간 마다 리뷰 평점을 업데이트
	@Scheduled(cron = "0 0 * * * ?")
	@Caching(evict = {
		@CacheEvict(cacheNames = "reviewCache", allEntries = true),
		@CacheEvict(cacheNames = "reviewSearchCache", allEntries = true)
	})
	public void fetchPerformanceRatingStatsBulk() {
		log.info("[Review][Scheduler] 공연 별 평균 평점 계산 및 캐시 반영 시작");

		try {
			redisRatingRepository.setAvgRatingForChangedPerformances();
			log.info("[Review][Scheduler] 평균 평점 계산 및 캐시 반영 완료");
		} catch (Exception e) {
			log.error("[Review][Scheduler] 평균 평점 계산 중 예외 발생: {}", e.getMessage(), e);
		}
	}

}
