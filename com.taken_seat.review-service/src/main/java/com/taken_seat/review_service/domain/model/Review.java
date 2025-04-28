package com.taken_seat.review_service.domain.model;

import java.util.UUID;

import com.taken_seat.common_service.entity.BaseTimeEntity;
import com.taken_seat.common_service.exception.customException.ReviewException;
import com.taken_seat.common_service.exception.enums.ResponseCode;
import com.taken_seat.review_service.application.dto.service.ReviewDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "p_review")
public class Review extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(columnDefinition = "BINARY(16)", updatable = false, nullable = false)
	private UUID id;

	@Column(columnDefinition = "BINARY(16)", nullable = false)
	private UUID performanceId;

	@Column(columnDefinition = "BINARY(16)", nullable = false)
	private UUID performanceScheduleId;

	@Column(columnDefinition = "BINARY(16)", nullable = false)
	private UUID authorId;

	@Column(nullable = false)
	private String authorEmail;

	@Column(nullable = false, length = 100)
	private String title;

	@Column(nullable = false, length = 100)
	private String content;

	@Column(nullable = false)
	private short rating;

	@Column(nullable = false)
	private Integer likeCount;

	public static Review create(ReviewDto reviewDto) {

		Review review = Review.builder()
			.performanceId(reviewDto.getPerformanceId())
			.performanceScheduleId(reviewDto.getPerformanceScheduleId())
			.authorId(reviewDto.getUserId())
			.authorEmail(reviewDto.getEmail())
			.title(reviewDto.getTitle())
			.content(reviewDto.getContent())
			.rating(reviewDto.getRating())
			.likeCount(0)
			.build();

		review.prePersist(reviewDto.getUserId());

		return review;
	}

	public void update(ReviewDto reviewDto) {
		this.title = reviewDto.getTitle();
		this.content = reviewDto.getContent();
		this.rating = reviewDto.getRating();
		this.preUpdate(reviewDto.getUserId());
	}

	public void updateLikeCount(int i) {
		int newCnt = this.likeCount + i;
		if (newCnt < 0) {
			throw new ReviewException(ResponseCode.INVALID_LIKE_COUNT);
		}
		this.likeCount = newCnt;
	}
}
