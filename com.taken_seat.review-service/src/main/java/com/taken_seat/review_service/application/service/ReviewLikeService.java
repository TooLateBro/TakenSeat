package com.taken_seat.review_service.application.service;

import java.util.Map;
import java.util.UUID;

import com.taken_seat.common_service.dto.AuthenticatedUser;

public interface ReviewLikeService {

	void toggleReviewLike(UUID id, AuthenticatedUser authenticatedUser);

	void increaseLikeCount(String key);

	void decreaseLikeCount(String key);

	Map<String, Integer> getAllReviewIdsWithLikes();

	void deleteAllReviewLikeKeys(Map<String, Integer> reviewLikesMap);

}
