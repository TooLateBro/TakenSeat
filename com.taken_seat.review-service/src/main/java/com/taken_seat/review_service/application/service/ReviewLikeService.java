package com.taken_seat.review_service.application.service;

import java.util.Map;

import com.taken_seat.review_service.application.dto.service.ReviewDto;

public interface ReviewLikeService {

	void toggleReviewLike(ReviewDto reviewDto);

	void increaseLikeCount(String key);

	void decreaseLikeCount(String key);

	Map<String, Integer> getAllReviewIdsWithLikes();

	void deleteAllReviewLikeKeys(Map<String, Integer> reviewLikesMap);

}
