package com.taken_seat.review_service.infrastructure.repository;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.redis.connection.RedisHashCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Repository;

import com.taken_seat.common_service.exception.customException.PaymentException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import com.taken_seat.review_service.application.service.ReviewChangeMaker;
import com.taken_seat.review_service.domain.repository.RedisRatingRepository;
import com.taken_seat.review_service.domain.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Repository
@Slf4j
public class RedisRatingRepositoryImpl implements RedisRatingRepository {

	private final ReviewRepository reviewRepository;
	private final ReviewChangeMaker reviewChangeMaker;
	private final RedisTemplate<String, Object> redisTemplate;
	private final RedisSerializer<String> serializer = new StringRedisSerializer();

	private final String AVG_RATING_KEY = "avgRating:";
	private static final String FIELD_AVG_RATING = "avgRating";
	private static final String FIELD_REVIEW_COUNT = "reviewCount";

	@Override
	public Double getAvgRating(UUID performanceId) {
		log.info("[Review] 평균 평점 조회 시작, performanceId={}", performanceId);
		String avgRatingKey = AVG_RATING_KEY + performanceId;

		Map<Object, Object> ratingData = redisTemplate.opsForHash().entries(avgRatingKey);

		double avgRating = getOrDefaultRating(ratingData, FIELD_AVG_RATING);
		long reviewCount = getOrDefaultReviewCountFromObjectKey(ratingData, FIELD_REVIEW_COUNT);

		if (avgRating == 0.0 || reviewCount < 50) {
			log.info("[Review] 평점이 없거나 리뷰 수가 적음, DB에서 평점 및 리뷰 수 조회 시작, performanceId={}", performanceId);
			Map<String, Object> avgRatingAndCount = reviewRepository.fetchAvgRatingAndReviewCountByPerformanceId(
				performanceId);

			saveRating(performanceId, avgRatingAndCount);

			avgRating = bigDecimalToDouble(avgRatingAndCount.get(FIELD_AVG_RATING));
			log.info("[Review] DB에서 평균 평점 및 리뷰 수 조회 완료, avgRating={}, reviewCount={}", avgRating, reviewCount);
		}

		return avgRating;
	}

	@Override
	public void setAvgRatingForChangedPerformances() {

		List<UUID> performanceIds = reviewChangeMaker.getChangedPerformanceIds();

		if (performanceIds.isEmpty()) {
			log.info("[Review] 변경된 공연 없음, 리뷰 평점 갱신 생략");
			return;
		}

		List<Map<String, Object>> avgRatingStats =
			reviewRepository.fetchAvgRatingAndReviewCountByPerformanceIds(performanceIds);

		if (avgRatingStats.isEmpty()) {
			log.info("[Review] 공연 ID에 대한 리뷰 통계 없음. 리뷰 평점 갱신 생략");
		}

		int batchSize = 1000;
		int totalRecords = avgRatingStats.size();

		for (int i = 0; i < totalRecords; i += batchSize) {
			int start = i;
			int end = Math.min(start + batchSize, totalRecords);

			List<Map<String, Object>> batchList = avgRatingStats.subList(start, end);
			log.info("[Review] Redis Pipeline 처리 시작 (start = {}, end = {})", start, end);
			redisTemplate.executePipelined((RedisCallback<Object>)connection -> {

				RedisHashCommands hashCommands = connection.hashCommands();
				for (Map<String, Object> stat : batchList) {
					UUID performanceId = bytesToUUID(stat.get("performanceId"));
					double avgRating = bigDecimalToDouble(stat.get(FIELD_AVG_RATING));
					long reviewCount = getOrDefaultReviewCountFromStringKey(stat, FIELD_REVIEW_COUNT);

					String avgRatingKey = AVG_RATING_KEY + performanceId;

					Map<byte[], byte[]> redisMap = new HashMap<>();
					redisMap.put(serializer.serialize(FIELD_AVG_RATING),
						serializer.serialize(String.valueOf(avgRating)));
					redisMap.put(serializer.serialize(FIELD_REVIEW_COUNT),
						serializer.serialize(String.valueOf(reviewCount)));

					connection.hMSet(serializer.serialize(avgRatingKey), redisMap);
					connection.expire(serializer.serialize(avgRatingKey), Duration.ofHours(2).getSeconds());

					hashCommands.hMSet(serializer.serialize(avgRatingKey), redisMap);

					connection.keyCommands().expire(
						serializer.serialize(avgRatingKey),
						Duration.ofHours(2).getSeconds()
					);
				}
				return null;
			});

			reviewChangeMaker.clearChangedPerformanceIds();
			log.info("[Review] Redis Pipeline 처리 완료");
		}
	}

	private double getOrDefaultRating(Map<Object, Object> ratingData, String field) {
		Object ratingObj = ratingData.get(field);
		return (ratingObj != null) ? (double)ratingObj : 0.0;
	}

	private long getOrDefaultReviewCountFromObjectKey(Map<Object, Object> ratingData, String field) {
		Object reviewCountObj = ratingData.get(field);

		if (reviewCountObj instanceof Long) {
			return (Long)reviewCountObj;
		} else if (reviewCountObj instanceof Integer) {
			return ((Integer)reviewCountObj).longValue();
		} else if (reviewCountObj instanceof String) {
			try {
				return Long.parseLong((String)reviewCountObj);
			} catch (NumberFormatException e) {
				log.warn("[Review] 문자열 리뷰 수 파싱 실패: {}", reviewCountObj);
			}
		}
		return 0L;
	}

	private long getOrDefaultReviewCountFromStringKey(Map<String, Object> ratingData, String field) {
		Object reviewCountObj = ratingData.get(field);

		if (reviewCountObj instanceof Long) {
			return (Long)reviewCountObj;
		} else if (reviewCountObj instanceof Integer) {
			return ((Integer)reviewCountObj).longValue();
		} else if (reviewCountObj instanceof String) {
			try {
				return Long.parseLong((String)reviewCountObj);
			} catch (NumberFormatException e) {
				log.warn("[Review] 문자열 리뷰 수 파싱 실패: {}", reviewCountObj);
			}
		}
		return 0L;
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
		log.error("[Review] 잘못된 값 형식, value={}", value);
		throw new PaymentException(ResponseCode.ILLEGAL_ARGUMENT);
	}

	private void saveRating(UUID performanceId, Map<String, Object> avgRatingAndCount) {
		double avgRating = bigDecimalToDouble(avgRatingAndCount.get(FIELD_AVG_RATING));
		long reviewCount = (long)avgRatingAndCount.get(FIELD_REVIEW_COUNT);

		saveRatingToRedisWithTTL(performanceId, avgRating, reviewCount);
		log.info("[Review] Redis에 평점 및 리뷰 수 저장, performanceId={}, avgRating={}, reviewCount={}", performanceId,
			avgRating, reviewCount);
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
		log.info("[Review] 평점 및 리뷰 수 Redis에 저장 완료, performanceId={}, avgRating={}, reviewCount={}", performanceId,
			avgRating, reviewCount);
	}
}
