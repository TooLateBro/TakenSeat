package com.taken_seat.review_service.application.dto.response;

import java.util.UUID;

import com.taken_seat.review_service.domain.model.Review;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDetailResDto {

	private UUID id;

	private UUID authorId;

	private UUID performanceId;

	private String title;

	private String content;

	private Integer likeCount;

	public static ReviewDetailResDto toResponse(Review review) {
		return ReviewDetailResDto.builder()
			.id(review.getId())
			.authorId(review.getAuthorId())
			.performanceId(review.getPerformanceId())
			.title(review.getTitle())
			.content(review.getContent())
			.likeCount(review.getLikeCount())
			.build();
	}
}
