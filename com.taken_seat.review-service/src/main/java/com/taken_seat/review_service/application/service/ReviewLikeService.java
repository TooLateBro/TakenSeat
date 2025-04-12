package com.taken_seat.review_service.application.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taken_seat.common_service.dto.AuthenticatedUser;
import com.taken_seat.common_service.exception.customException.ReviewException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import com.taken_seat.review_service.domain.model.Review;
import com.taken_seat.review_service.domain.model.ReviewLike;
import com.taken_seat.review_service.domain.repository.ReviewLikeRepository;
import com.taken_seat.review_service.domain.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewLikeService {

	private final ReviewLikeRepository reviewLikeRepository;
	private final ReviewRepository reviewRepository;

	public void toggleReviewLike(UUID id, AuthenticatedUser authenticatedUser) {

		Review review = reviewRepository.findWithPessimisticLockById(id)
			.orElseThrow(() -> new ReviewException(ResponseCode.REVIEW_NOT_FOUND));

		Optional<ReviewLike> reviewLikeOpt = reviewLikeRepository.findByAuthorIdAndReviewId(
			authenticatedUser.getUserId(), id);

		if (reviewLikeOpt.isEmpty()) {
			reviewLikeRepository.save(ReviewLike.create(review, authenticatedUser.getUserId()));
			review.updateLikeCount(1);
		} else if (reviewLikeOpt.get().isDeleted()) {
			
			reviewLikeOpt.get().addReviewLike(authenticatedUser.getUserId());
			review.updateLikeCount(1);
		} else {
			// 이미 좋아요가 눌려있으면 취소
			reviewLikeOpt.get().cancelReviewLike(authenticatedUser.getUserId());
			review.updateLikeCount(-1);
		}

	}
}
