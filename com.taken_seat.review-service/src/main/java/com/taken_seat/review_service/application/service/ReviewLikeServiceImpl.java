package com.taken_seat.review_service.application.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taken_seat.common_service.exception.customException.ReviewException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import com.taken_seat.review_service.application.dto.service.ReviewDto;
import com.taken_seat.review_service.domain.model.Review;
import com.taken_seat.review_service.domain.model.ReviewLike;
import com.taken_seat.review_service.domain.repository.ReviewLikeRepository;
import com.taken_seat.review_service.domain.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ReviewLikeServiceImpl implements ReviewLikeService {

	private final RedisTemplate<String, Integer> likeCountRedisTemplate;
	private final ReviewLikeRepository reviewLikeRepository;
	private final ReviewRepository reviewRepository;

	private static final String LIKE_KEY_PREFIX = "review:like:";
	private static final String USER_FIELD_PREFIX = "user:";

	private HashOperations<String, String, Object> hashOps;

	@Override
	public void toggleReviewLike(ReviewDto reviewDto) {
		UUID reviewId = reviewDto.getReviewId();
		UUID userId = reviewDto.getUserId();

		hashOps = likeCountRedisTemplate.opsForHash();
		String key = getRedisKey(reviewId);
		String userField = getUserField(userId);

		Optional<ReviewLike> reviewLikeOpt = reviewLikeRepository.findByAuthorIdAndReviewId(
			userId, reviewId);

		if (reviewLikeOpt.isPresent() && !reviewLikeOpt.get().isDeleted()) {
			// 이미 좋아요를 누른 경우
			hashOps.delete(key, userField);
			decreaseLikeCount(key);

			ReviewLike reviewLike = reviewLikeOpt.get();
			reviewLike.cancelReviewLike(userId);
			reviewLikeRepository.save(reviewLike);

			log.info("[Review] 좋아요 취소 시도 - 성공 userId={}, reviewId={}", userId, reviewId);
		} else {
			// 좋아요를 안한 상태 -> 추가
			hashOps.put(key, userField, true);
			increaseLikeCount(key);

			ReviewLike like = reviewLikeOpt
				.map(l -> {
					l.addReviewLike(userId); // soft delete 되어 있던 것 복구
					log.info("[Review] 좋아요 복구 시도 - 성공 userId={}, reviewId={}", userId, reviewId);
					return l;
				})
				.orElseGet(() -> {
					log.info("[Review] 좋아요 생성 시도 - 성공 userId={}, reviewId={}", userId, reviewId);
					return ReviewLike.create(findReview(reviewId), userId);
				});

			reviewLikeRepository.save(like);
		}

	}

	// 아래 두 메서드는 주기적인 스케줄러에서 DB에 반영할 때 호출
	@Override
	public void increaseLikeCount(String key) {
		hashOps.increment(key, "count", 1);
	}

	@Override
	public void decreaseLikeCount(String key) {
		hashOps.increment(key, "count", -1);  // -1을 넘기면 감소
	}

	// Redis에서 모든 리뷰의 좋아요 수를 가져와 HashMap으로 반환
	@Override
	public Map<String, Integer> getAllReviewIdsWithLikes() {
		hashOps = likeCountRedisTemplate.opsForHash();
		String pattern = "review:like:*";

		RedisConnection connection = likeCountRedisTemplate.getConnectionFactory().getConnection();
		ScanOptions options = ScanOptions.scanOptions()
			.match(pattern)
			.count(1000)  // 한 번에 가져올 키 개수
			.build();

		// SCAN을 통해 Redis에서 키를 가져옴
		Cursor<byte[]> keys = connection.scan(options);

		// 결과를 저장할 맵
		Map<String, Integer> reviewLikesMap = new HashMap<>();

		// SCAN을 통해 가져온 키들에 대해 처리
		while (keys.hasNext()) {
			// Redis에서 가져온 키 (byte[])를 문자열로 변환
			String key = new String(keys.next());

			// Redis에서 해당 키에 대한 좋아요 수를 가져옴
			Integer likeCount = getLikeCountFromRedis(key);

			// UUID 만 분리
			key = key.replace(LIKE_KEY_PREFIX, "");

			// reviewLikesMap에 키와 좋아요 수를 추가
			reviewLikesMap.put(key, likeCount);
		}

		// SCAN이 끝난 후, 연결 자원을 닫음
		connection.close();
		return reviewLikesMap;
	}

	@Override
	public void deleteAllReviewLikeKeys(Map<String, Integer> reviewLikesMap) {
		if (reviewLikesMap.isEmpty())
			return;

		// Redis에 실제 저장된 키 형식으로 복원
		List<String> redisKeys = reviewLikesMap.keySet().stream()
			.map(id -> LIKE_KEY_PREFIX + id)
			.toList();

		// Redis에서 해당 키들 삭제
		likeCountRedisTemplate.delete(redisKeys);
		log.info("[Review] 좋아요 캐시 삭제 요청 - 완료 deletedCount={}", redisKeys.size());
	}

	// Redis에서 특정 키에 대한 좋아요 수를 가져오는 메서드
	private Integer getLikeCountFromRedis(String key) {
		hashOps = likeCountRedisTemplate.opsForHash();

		Integer count = (Integer)hashOps.get(key, "count");

		// "count" 값이 있으면 Integer로 변환하여 반환, 없으면 기본값 0 반환
		if (count != null) {
			log.debug("[Review] 좋아요 수 조회 - 성공 key={}, count={}", key, count);
			return count;
		} else {
			log.warn("[Review] 좋아요 수 조회 - 실패 key={}, count=0 ", key);
			return 0; // 기본값 0 반환
		}
	}

	private String getRedisKey(UUID reviewId) {
		return LIKE_KEY_PREFIX + reviewId;
	}

	private String getUserField(UUID userId) {
		return USER_FIELD_PREFIX + userId;
	}

	private Review findReview(UUID reviewId) {
		return reviewRepository.findByIdAndDeletedAtIsNull(reviewId)
			.orElseThrow(() -> new ReviewException(ResponseCode.REVIEW_NOT_FOUND));
	}
}
