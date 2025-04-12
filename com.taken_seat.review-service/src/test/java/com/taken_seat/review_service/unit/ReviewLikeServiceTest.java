package com.taken_seat.review_service.unit;

import static org.mockito.Mockito.*;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import com.taken_seat.common_service.dto.AuthenticatedUser;
import com.taken_seat.review_service.infrastructure.service.ReviewLikeServiceImpl;

@ExtendWith(MockitoExtension.class)
public class ReviewLikeServiceTest {

	@InjectMocks
	private ReviewLikeServiceImpl reviewLikeService;

	@Mock
	private RedisTemplate<String, Integer> likeCountRedisTemplate;

	@Mock
	private HashOperations<String, Object, Object> hashOperations;

	private UUID reviewId;
	private UUID userId;
	private AuthenticatedUser user;

	@BeforeEach
	void setUp() {
		reviewId = UUID.randomUUID();
		userId = UUID.randomUUID();
		user = new AuthenticatedUser(userId, "user@example.com", "USER");

		when(likeCountRedisTemplate.opsForHash()).thenReturn(hashOperations);
	}

	@Test
	@DisplayName("\"리뷰에 좋아요를 처음 누르면 좋아요가 등록되고 count가 1 증가한다- SUCCESS")
	void testToggleReviewLike_increaseLikeCount() {
		// given
		String key = "review:like:" + reviewId;
		String userField = "user:" + userId;

		when(hashOperations.hasKey(key, userField)).thenReturn(false); // 좋아요를 누르지 않은 상태

		// when
		reviewLikeService.toggleReviewLike(reviewId, user);

		// then
		verify(hashOperations).put(key, userField, true); // 좋아요 등록
		verify(hashOperations).increment(key, "count", 1); // count 증가
	}

	@Test
	@DisplayName("이미 좋아요를 누른 상태에서 다시 누르면 좋아요가 삭제되고 count가 1 감소한다  - SUCCESS")
	void testToggleReviewLike_decreaseLikeCount() {
		// given
		String key = "review:like:" + reviewId;
		String userField = "user:" + userId;

		when(hashOperations.hasKey(key, userField)).thenReturn(true); // 이미 좋아요를 누른 상태

		// when
		reviewLikeService.toggleReviewLike(reviewId, user);

		// then
		verify(hashOperations).delete(key, userField); // 좋아요 삭제
		verify(hashOperations).increment(key, "count", -1); // count 감소
	}
}
