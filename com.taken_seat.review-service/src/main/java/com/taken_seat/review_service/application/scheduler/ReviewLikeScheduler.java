package com.taken_seat.review_service.application.scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.taken_seat.review_service.application.service.ReviewLikeService;
import com.taken_seat.review_service.domain.model.Review;
import com.taken_seat.review_service.domain.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewLikeScheduler {

	private final ReviewLikeService reviewLikeService;
	private final ReviewRepository reviewRepository;

	@Scheduled(cron = "0 */5 * * * ?")  // 매 5분마다 실행
	public void updateReviewLikeCounts() {
		// Redis에서 모든 리뷰와 좋아요 수를 가져옴 (Hash 형태로)
		Map<String, Integer> reviewLikesMap = reviewLikeService.getAllReviewIdsWithLikes();

		// 저장할 리뷰 목록을 준비
		List<Review> reviewsToUpdate = new ArrayList<>();

		// 가져온 리뷰와 좋아요 수에 대해 DB 업데이트
		reviewLikesMap.forEach((reviewId, likeCount) -> {
			UUID reviewUUID = UUID.fromString(reviewId);  // Redis에서 가져온 String을 UUID로 변환

			// DB에서 해당 리뷰를 찾아 좋아요 수 업데이트
			reviewRepository.findByIdAndDeletedAtIsNull(reviewUUID).ifPresent(review -> {
				review.updateLikeCount(likeCount);
				reviewsToUpdate.add(review);  // 업데이트된 리뷰를 리스트에 추가
			});
		});

		// 모든 리뷰를 일괄적으로 DB에 저장
		if (!reviewsToUpdate.isEmpty()) {
			reviewRepository.saveAllAndFlush(reviewsToUpdate);
			reviewLikeService.deleteAllReviewLikeKeys(reviewLikesMap);
		}

	}

}
