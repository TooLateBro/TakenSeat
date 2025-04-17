package com.taken_seat.review_service.infrastructure.repository;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.taken_seat.common_service.exception.customException.PaymentException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import com.taken_seat.review_service.domain.repository.RedisRatingRepository;
import com.taken_seat.review_service.domain.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Repository
@Slf4j
public class RedisRatingRepositoryImpl implements RedisRatingRepository {

	private final ReviewRepository reviewRepository;
	private final RedisTemplate<String, Object> redisTemplate;

	private final String AVG_RATING_KEY = "avgRating:";
	private static final String FIELD_AVG_RATING = "avgRating";
	private static final String FIELD_REVIEW_COUNT = "reviewCount";

	@Override
	public Double getAvgRating(UUID performanceId) {

		String avgRatingKey = AVG_RATING_KEY + performanceId;

		Map<Object, Object> ratingData = redisTemplate.opsForHash().entries(avgRatingKey);

		double avgRating = getOrDefaultRating(ratingData, FIELD_AVG_RATING);
		long reviewCount = getOrDefaultReviewCount(ratingData, FIELD_REVIEW_COUNT);

		if (avgRating == 0.0 || reviewCount < 50) {
			Map<String, Object> avgRatingAndCount = reviewRepository.fetchAvgRatingAndReviewCountByPerformanceId(
				performanceId);

			saveRating(performanceId, avgRatingAndCount);

			avgRating = bigDecimalToDouble(avgRatingAndCount.get(FIELD_AVG_RATING));
		}

		return avgRating;
	}

	@Override
	public void setAvgRatingBulk() {

		// DB에서 각 공연 아이디 별 평점과 리뷰의 수를 가져온다
		List<Map<String, Object>> performanceReviews = reviewRepository.fetchPerformanceRatingStatsBulk();

		// Redis pipline 을 잘라서 처리할 단위 설정
		int batchSize = 1000;

		// 조회된 데이터의 수
		int totalRecords = performanceReviews.size();

		// batchSize 단위로 나눠서 처리
		for (int i = 0; i < totalRecords; i += batchSize) {
			// start ~ end 범위 데이터만 처리
			int start = i;
			int end = Math.min(start + batchSize, totalRecords);

			// Redis Pipeline 시작
			redisTemplate.executePipelined((RedisCallback<Object>)connect -> {
				for (int j = start; j < end; j++) {
					Map<String, Object> map = performanceReviews.get(j);

					UUID performanceId = bytesToUUID(map.get("performanceId"));
					double avgRating = bigDecimalToDouble(map.get(FIELD_AVG_RATING));
					long reviewCount = (long)map.get(FIELD_REVIEW_COUNT);

					saveRatingToRedisWithTTL(performanceId, avgRating, reviewCount);
				}
				return null;
			});

		}

	}

	private double getOrDefaultRating(Map<Object, Object> ratingData, String field) {
		Object ratingObj = ratingData.get(field);
		return (ratingObj != null) ? (double)ratingObj : 0.0;
	}

	private long getOrDefaultReviewCount(Map<Object, Object> ratingData, String field) {
		Object reviewCountObj = ratingData.get(field);

		if (reviewCountObj instanceof Long) {
			return (Long)reviewCountObj;
		} else if (reviewCountObj instanceof Integer) {
			return ((Integer)reviewCountObj).longValue(); // Integer → Long 변환
		} else {
			return 0L;
		}

	}

	private UUID bytesToUUID(Object value) {
		byte[] uuidByte = (byte[])value;
		if (uuidByte == null || uuidByte.length != 16) {
			throw new PaymentException(ResponseCode.ILLEGAL_ARGUMENT, "잘못된 UUID 입니다.");
		}
		ByteBuffer bb = ByteBuffer.wrap(uuidByte);
		return new UUID(bb.getLong(), bb.getLong());
	}

	private double bigDecimalToDouble(Object value) {
		if (value instanceof BigDecimal) {
			return ((BigDecimal)value).doubleValue();
		}
		throw new PaymentException(ResponseCode.ILLEGAL_ARGUMENT);
	}

	private void saveRating(UUID performanceId, Map<String, Object> avgRatingAndCount) {
		double avgRating = bigDecimalToDouble(avgRatingAndCount.get(FIELD_AVG_RATING));
		long reviewCount = (long)avgRatingAndCount.get(FIELD_REVIEW_COUNT);

		saveRatingToRedisWithTTL(performanceId, avgRating, reviewCount);
	}

	private void saveRatingToRedisWithTTL(UUID performanceId, double avgRating, long reviewCount) {
		String avgRatingKey = AVG_RATING_KEY + performanceId;

		Map<String, Object> ratingInfo = new HashMap<>();
		ratingInfo.put(FIELD_AVG_RATING, avgRating);
		ratingInfo.put(FIELD_REVIEW_COUNT, reviewCount);

		// 해시로 저장
		redisTemplate.opsForHash().putAll(avgRatingKey, ratingInfo);

		// TTL 설정: 1시간 30분
		redisTemplate.expire(avgRatingKey, Duration.ofHours(2));
	}
}
