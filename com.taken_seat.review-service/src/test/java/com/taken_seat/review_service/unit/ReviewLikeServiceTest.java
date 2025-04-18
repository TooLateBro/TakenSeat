package com.taken_seat.review_service.unit;

import static org.mockito.Mockito.*;

import java.util.Optional;
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
import com.taken_seat.review_service.application.service.ReviewLikeServiceImpl;
import com.taken_seat.review_service.domain.model.Review;
import com.taken_seat.review_service.domain.model.ReviewLike;
import com.taken_seat.review_service.domain.repository.ReviewLikeRepository;
import com.taken_seat.review_service.domain.repository.ReviewRepository;

@ExtendWith(MockitoExtension.class)
public class ReviewLikeServiceTest {

	@InjectMocks
	private ReviewLikeServiceImpl reviewLikeService;

	@Mock
	private RedisTemplate<String, Integer> likeCountRedisTemplate;

	@Mock
	private HashOperations<String, Object, Object> hashOperations;

	@Mock
	private ReviewRepository reviewRepository;

	@Mock
	private ReviewLikeRepository reviewLikeRepository;

	private UUID reviewId;
	private UUID userId;
	private AuthenticatedUser user;

	@BeforeEach
	void setUp() {
		reviewId = UUID.randomUUID();
		userId = UUID.randomUUID();
		user = new AuthenticatedUser(userId, "user@example.com", "MASTER");

		when(likeCountRedisTemplate.opsForHash()).thenReturn(hashOperations);
	}

	@Test
	@DisplayName("리뷰에 좋아요를 처음 누르면 좋아요가 등록되고 count가 1 증가한다- SUCCESS")
	void testToggleReviewLike_increaseLikeCount() {
		// given
		String key = "review:like:" + reviewId;
		String userField = "user:" + userId;

		// DB에 좋아요 기록 없음 (처음 누름)
		when(reviewLikeRepository.findByAuthorIdAndReviewId(userId, reviewId)).thenReturn(Optional.empty());

		// 리뷰 존재 mock
		Review dummyReview = mock(Review.class);
		when(reviewRepository.findByIdAndDeletedAtIsNull(reviewId)).thenReturn(Optional.of(dummyReview));

		// when
		reviewLikeService.toggleReviewLike(reviewId, user);

		// then
		verify(hashOperations).put(key, userField, true); // 좋아요 등록
		verify(hashOperations).increment(key, "count", 1); // count 증가
		verify(reviewLikeRepository).save(any(ReviewLike.class)); // DB에 저장
	}

	@Test
	@DisplayName("이미 좋아요를 누른 상태에서 다시 누르면 좋아요가 삭제되고 count가 1 감소한다  - SUCCESS")
	void testToggleReviewLike_decreaseLikeCount() {
		// given
		String key = "review:like:" + reviewId;
		String userField = "user:" + userId;

		ReviewLike like = mock(ReviewLike.class);
		when(like.isDeleted()).thenReturn(false); // soft delete 아님 → 아직 좋아요 상태

		when(reviewLikeRepository.findByAuthorIdAndReviewId(userId, reviewId))
			.thenReturn(Optional.of(like));

		// when
		reviewLikeService.toggleReviewLike(reviewId, user);

		// then
		verify(hashOperations).delete(key, userField);            // 좋아요 제거
		verify(hashOperations).increment(key, "count", -1);       // count 감소
		verify(like).cancelReviewLike(userId);                    // soft delete 호출
		verify(reviewLikeRepository).save(like);                  // DB 저장
	}
}
