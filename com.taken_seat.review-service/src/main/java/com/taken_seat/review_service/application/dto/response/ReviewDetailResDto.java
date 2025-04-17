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

	private UUID performanceId;

	private UUID performanceScheduleId;

	private UUID authorId;

	private String authorEmail;

	private String title;

	private String content;

	private short rating;

	private Integer likeCount;

	public static ReviewDetailResDto toResponse(Review review) {
		return ReviewDetailResDto.builder()
			.id(review.getId())
			.performanceId(review.getPerformanceId())
			.performanceScheduleId(review.getPerformanceScheduleId())
			.authorId(review.getAuthorId())
			.authorEmail(review.getAuthorEmail())
			.title(review.getTitle())
			.content(review.getContent())
			.rating(review.getRating())
			.likeCount(review.getLikeCount())
			.build();
	}
}
